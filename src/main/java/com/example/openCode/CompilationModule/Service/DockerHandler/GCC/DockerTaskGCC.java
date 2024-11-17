package com.example.openCode.CompilationModule.Service.DockerHandler.GCC;

import com.example.openCode.CompilationModule.Model.Task.ReturnType;
import com.example.openCode.CompilationModule.Model.Task.FunctionArgument;
import com.example.openCode.CompilationModule.Model.Task.Task;
import com.example.openCode.CompilationModule.Model.Task.TestTask.TestArgument;
import com.example.openCode.CompilationModule.Model.Task.TestTask.TestTask;
import com.example.openCode.CompilationModule.Service.DockerHandler.ContainerIdList;
import com.example.openCode.CompilationModule.Service.DockerHandler.DockerConfiguration;
import com.example.openCode.CompilationModule.Service.DockerHandler.DockerTaskLanguage;
import com.example.openCode.CompilationModule.Service.DockerHandler.MyResultCallback;
import com.example.openCode.CompilationModule.Service.Task.TaskService;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.sun.jdi.connect.Connector;
import org.bouncycastle.oer.its.etsi102941.FullCtl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.transform.Source;
import java.util.Iterator;

import static com.example.openCode.CompilationModule.Service.Task.TaskService.isTaskArrayType;
import static com.example.openCode.CompilationModule.Service.Task.TaskService.isTypeAnArrayType;

@Component
public class DockerTaskGCC implements DockerTaskLanguage {

    //This class role is to create code tasks inside a docker container that can
    //be run to check if the solution of the user is correct

    private static final Logger log = LoggerFactory.getLogger(DockerTaskGCC.class);
    private DockerClient dockerClient = DockerConfiguration.getDockerClientInstance();
    private String gccContainerId = ContainerIdList.getGccContainerId();


    public void createTaskInContainer(Task task) {
        if (TaskService.isTaskReadyForCreation(task)) {
            String catalogName = task.getCatalogName();
            createTaskFiles(task, catalogName);
            StringBuilder testCodeForUser = new StringBuilder();
            StringBuilder testCodeForTests = new StringBuilder();
            generateTaskCodeForUser(task, testCodeForUser, true);
            generateTaskCodeForTests(task, testCodeForTests, catalogName);

            ExecCreateCmdResponse createTaskDocker2 = dockerClient.execCreateCmd(gccContainerId)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .withAttachStdin(true)
                    .withCmd("sh", "-c", "printf '%s' '" + testCodeForTests + "' > tmp/" + catalogName + "/test.c &&" +
                            "printf '%s' '" + testCodeForUser + "' > tmp/" + catalogName + "/" + task.getFunctionName() + ".c"
                    )
                    .exec();


            MyResultCallback callback = new MyResultCallback();
            dockerClient.execStartCmd(createTaskDocker2.getId()).exec(callback);
            try {
                callback.awaitCompletion();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(callback.getOutput());
        }
    }


    public Boolean isTaskCreatedInDockerContainer(Task task) {
        String catalogName = task.getId() + "-" + task.getFunctionName();
        String checkCmd = "[ -d tmp/" + catalogName + " ] && " +
                "[ -f tmp/" + catalogName + "/test.c ] && " +
                "[ -f tmp/" + catalogName + "/" + task.getFunctionName() + ".c ] && echo 'true' || echo 'false'";

        try {
            // Wykonanie jednego zapytania do sprawdzenia katalogu i plików
            ExecCreateCmdResponse checkCmdResponse = dockerClient.execCreateCmd(gccContainerId)
                    .withAttachStderr(true)
                    .withAttachStdout(true)
                    .withCmd("sh", "-c", checkCmd)
                    .exec();
            MyResultCallback callback = new MyResultCallback();
            dockerClient.execStartCmd(checkCmdResponse.getId()).exec(callback);
            callback.awaitCompletion();

            // Odczytanie wyniku
            String output = callback.getOutput().trim();
            return "true".equals(output);

        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void createTaskFiles(Task task, String catalog) {
        ExecCreateCmdResponse execCreateTaskFiles = dockerClient.execCreateCmd(gccContainerId)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withCmd("sh", "-c",
                        "mkdir -p tmp/" + catalog + " && " + // Tworzenie katalogu z flagą -p (jeśli nie istnieje)
                                "rm -f tmp/" + catalog + "/* && " +  // Usuwanie istniejących plików (jeśli są)
                                "touch tmp/" + catalog + "/test.c tmp/" + catalog + "/" + task.getFunctionName() + ".c" // Tworzenie nowych plików
                )
                .exec();
        MyResultCallback createTaskFilesCallback = new MyResultCallback();
        dockerClient.execStartCmd(execCreateTaskFiles.getId()).exec(createTaskFilesCallback);
        try {
            createTaskFilesCallback.awaitCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(createTaskFilesCallback.getOutput());
    }

    private void generateTaskCodeForUser(Task task, StringBuilder builder, boolean withBrackets) {
        //StringBuilder builder = new StringBuilder();
        if(isTaskArrayType(task)){
            builder.append("/**\n" +
                    " * Note: The returned array must be malloced, assume caller calls free().\n" +
                    " */\n");
            builder.append("#include <stdlib.h>\n");
        }
        builder.append(getTypeToString(task.getReturnType())); //RETURN TYPE
        builder.append(" ").append(task.getFunctionName()).append("(");

        Iterator<FunctionArgument> iterator = task.getArgumentList().iterator();
        FunctionArgument functionArgumentTMP;
        while (iterator.hasNext()) {
            functionArgumentTMP = iterator.next();
            builder.append(getTypeToString(functionArgumentTMP.getType())).append(" ").append(functionArgumentTMP.getName());

            if(isTypeAnArrayType(functionArgumentTMP.getType())){ //Adding size to arrayTypes
                builder.append(", ");
                builder.append("int* size").append(functionArgumentTMP.getName());
                builder.append(" ");
            }

            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }

        if(isTypeAnArrayType(task.getReturnType())){
            builder.append(", int* sizeResult");
        }

        if (withBrackets) {
            builder.append(")\n{\n}");
        } else {
            builder.append(");\n");
        }
    }

    private void generateTaskCodeForTests(Task task, StringBuilder builder, String catalog) {
        //StringBuilder builder = new StringBuilder();
        //todo: dodac wyciaganie danych z taska zeby za kazdym razem nie uzywac iteratora jakos czytelniej??
        builder.append("#include <stdio.h>\n");
        builder.append("#include <stdbool.h>\n");
        builder.append("#include <string.h>\n");
        builder.append("#include <stdlib.h>\n");
        generateTaskCodeForUser(task, builder, false);

        builder.append("#define OPERATION \"");
        builder.append(task.getFunctionName());
        builder.append("\"\n");

        generateTestFunctionToDocker(task, builder);

        generateTestMainFunction(task, builder);
    }

    private void generateTestFunctionToDocker(Task task, StringBuilder builder) {
        generateTestFunctionHeader(task, builder);

        generateTestFunctionBody(task, builder);
    }

    private static void generateTestMainFunction(Task task, StringBuilder builder) {
        builder.append("int main(){\n");
        builder.append("\tint testOverall = 0;\n");
        builder.append("\tint testFailed = 0;\n");

        generateTestFunctionCallsWithArguments(task, builder);

        builder.append("\tprintf(\" overall: %d, failed %d \\n\", testOverall, testFailed);\n");

        builder.append("\treturn 0;\n");

        builder.append("}");
    }


    // ----------------- BUILDER MODIFIERS (TEST FUNCTION)-----------------------

    private static void generateTestFunctionHeader(Task task, StringBuilder builder) {
        builder.append("void test(");
        builder.append(getTypeToString(task.getReturnType())); // return type
        builder.append(" (*operation)(");

        Iterator<FunctionArgument> iterator = task.getArgumentList().iterator();
        FunctionArgument functionArgumentTMP;

        while (iterator.hasNext()) { // argumenty po operation dla "lambdy" void test(typ (*operation)(brakujaca zawartosc...)
            functionArgumentTMP = iterator.next();
            builder.append(getTypeToString(functionArgumentTMP.getType()));
            if(isTypeAnArrayType(functionArgumentTMP.getType())){
                builder.append(", ").append("int*"); //size[NazwaZmiennej]
            }
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        if (isTaskArrayType(task)){
            builder.append(", ").append("int*"); //sizeResult
        }

        iterator = task.getArgumentList().iterator();
        builder.append("),");
        while (iterator.hasNext()) { //argumenty testowe i oczekiwany wynik
            functionArgumentTMP = iterator.next();
            builder.append(getTypeToString(functionArgumentTMP.getType()));
            builder.append(" ");
            builder.append(functionArgumentTMP.getName());
            builder.append(","); //Lack of hasNext check is on purpose
            if(isTypeAnArrayType(functionArgumentTMP.getType())){
                builder.append("int* size").append(functionArgumentTMP.getName());
                builder.append(" ,");
            }
        }
        if(isTaskArrayType(task)){
            builder.append("int* sizeExpected, ");
        }
        builder.append(getTypeToString(task.getReturnType()));
        builder.append(" expected,int* overall,int* failed){\n"); //space is necessary
    }

    private static void generateTestFunctionBody(Task task, StringBuilder builder) {
        Iterator<FunctionArgument> iterator;
        FunctionArgument functionArgumentTMP;


        builder.append("\tint sizeResult = -1;\n");
        builder.append("\t");
        builder.append(getTypeToString(task.getReturnType()));
        builder.append(" result = operation(");
        iterator = task.getArgumentList().iterator();
        while (iterator.hasNext()) {
            functionArgumentTMP = iterator.next();
            builder.append(functionArgumentTMP.getName());
            if(isTypeAnArrayType(functionArgumentTMP.getType())){
                builder.append(",");
                builder.append("size").append(functionArgumentTMP.getName());
            }
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        if(isTypeAnArrayType(task.getReturnType())){
            builder.append(",&sizeResult");
        }
        builder.append(");\n");

        builder.append("\t*overall = *overall + 1;\n");


        generateTestResultComparisone(task, builder);

        generateTestResultPassed(task,builder);

        builder.append("\t} else {\n");

        generateTestResultFailed(task,builder);

        builder.append("\t\t*failed = *failed + 1;\n");

        builder.append("\t}\n");

        if(isTaskArrayType(task)){
            builder.append("\tfree(result);\n");
        }

        builder.append("}\n");
    }

    //TODO: Poprawić porównywanie tablic sizeof nie działa zostawić na samym memcmp bez przypadku za dużej tablicy użytkownika
    private static void generateTestResultComparisone(Task task, StringBuilder builder) {
        if (task.getReturnType() == ReturnType.INTVECTOR) {
            //mozna wykorzystać biblioteke string.h do tablic moze 2 wymiarowe tez?
            builder.append("\tif(memcmp(expected,result,(size_t) sizeExpected) == 0 && *sizeExpected == sizeResult) {\n"); //todo: sizeof do wyrzucenia
        } else if (task.getReturnType() == ReturnType.STRING || task.getReturnType() == ReturnType.CHARVECTOR) {
            builder.append("\tif(strcmp(expected,result) == 0 && sizeExpected == sizeResult) { \n");
        } else {
            builder.append("\tif(result == expected) {\n");
        }
    }

    private static void generateTestResultPassed(Task task, StringBuilder builder){
        if(isTaskArrayType(task)){
            builder.append("\t\tprintf(\"\\nTest passed: %s(\",OPERATION);\n");

            builder.append("\t\tprintf(\"result: [\");\n");
            builder.append("\t\t\tfor(int i = 0 ; i < sizeResult; i++){\n");
            builder.append("\t\t\t\tprintf(\"%d,\",result[i]);\n");
            builder.append("\t\t\t}\n");
            builder.append("\t\tprintf(\"] \");\n");

            builder.append("\t\tprintf(\"expected: [\");\n");
            builder.append("\t\t\tfor(int i = 0 ; i < *sizeExpected; i++){\n");
            builder.append("\t\t\t\tprintf(\"%d,\",expected[i]);\n");
            builder.append("\t\t\t}\n");
            builder.append("\t\tprintf(\"]) \");\n");

        }else {
            Iterator<FunctionArgument> iterator = task.getArgumentList().iterator();
            FunctionArgument functionArgumentTMP;

            builder.append("\t\tprintf(\"Test passed: %s(");
            iterator = task.getArgumentList().iterator();
            while (iterator.hasNext()) {
                functionArgumentTMP = iterator.next();
                builder.append(printfSpecifiers(functionArgumentTMP.getType()));
                if (iterator.hasNext()) {
                    builder.append(", ");
                }
            }
            builder.append(") == ");
            builder.append(printfSpecifiers(task.getReturnType()));
            builder.append("\\n\",OPERATION,");
            iterator = task.getArgumentList().iterator();
            while (iterator.hasNext()) {
                functionArgumentTMP = iterator.next();
                builder.append(functionArgumentTMP.getName());
                builder.append(",");
            }
            builder.append("expected);\n");
        }
    }

    private static void generateTestResultFailed(Task task, StringBuilder builder){
        if(isTaskArrayType(task)){
            builder.append("\t\tprintf(\"\\nTest failed: %s(\",OPERATION);\n");
            builder.append("\t\tprintf(\"result: [\");\n");
            builder.append("\t\t\tfor(int i = 0 ; i < sizeResult; i++){\n");
            builder.append("\t\t\t\tprintf(\"%d,\",result[i]);\n");
            builder.append("\t\t\t}\n");
            builder.append("\t\tprintf(\"] \");\n");
            builder.append("\t\tprintf(\"expected: [\");\n");
            builder.append("\t\t\tfor(int i = 0 ; i < *sizeExpected; i++){\n");
            builder.append("\t\t\t\tprintf(\"%d,\",expected[i]);\n");
            builder.append("\t\t\t}\n");
            builder.append("\t\tprintf(\"]) \");\n");
        }else {
            Iterator<FunctionArgument> iterator = task.getArgumentList().iterator();
            FunctionArgument functionArgumentTMP;

            builder.append("\t\tprintf(\"Test failed: %s(");
            iterator = task.getArgumentList().iterator();
            while (iterator.hasNext()) {
                functionArgumentTMP = iterator.next();
                builder.append(printfSpecifiers(functionArgumentTMP.getType()));
                if (iterator.hasNext()) {
                    builder.append(", ");
                }
            }
            builder.append(") == ");
            builder.append(printfSpecifiers(task.getReturnType()));
            builder.append(", got ");
            builder.append(printfSpecifiers(task.getReturnType()));
            builder.append(" instead\\n\",OPERATION,");
            iterator = task.getArgumentList().iterator();
            while (iterator.hasNext()) {
                functionArgumentTMP = iterator.next();
                builder.append(functionArgumentTMP.getName());
                builder.append(", ");
            }
            builder.append("expected, result);\n");
        }
    }

    private static void generateTestResultPrintfArray(String data, StringBuilder builder){

    }


    // ----------------- BUILDER MODIFIERS (MAIN FUNCTION)-----------------------


    private static void generateTestFunctionCallsWithArguments(Task task, StringBuilder builder) {
        Iterator<TestTask> testIterator = task.getTestList().iterator();
        Iterator<TestArgument> testArgumentIterator;

        TestTask testTaskTMP;
        TestArgument testArgument;

        while (testIterator.hasNext()) {
            testTaskTMP = testIterator.next();
            builder.append("\ttest(");
            builder.append(task.getFunctionName());
            builder.append(", ");
            testArgumentIterator = testTaskTMP.getTestArguments().iterator();

            while (testArgumentIterator.hasNext()) {
                testArgument = testArgumentIterator.next();
                generateArgumentsForTest(testArgument, builder);
            }

            //Return value for a test
            generateReturnValueForTest(testTaskTMP, builder);
        }
    }

    private static void generateReturnValueForTest(TestTask testTaskTMP, StringBuilder builder) {
        //Return value for a test
        if (testTaskTMP.getTask().getReturnType() == ReturnType.STRING) {
            builder.append("\"");
            builder.append(testTaskTMP.getExpectedValue());
            builder.append("\"");
        } else if (testTaskTMP.getTask().getReturnType() == ReturnType.CHAR) {
            builder.append("'\\''");
            builder.append(testTaskTMP.getExpectedValue());
            builder.append("'\\''");
        } else if (testTaskTMP.getTask().getReturnType() == ReturnType.INTVECTOR) {
            builder.append("(int*) ").append(testTaskTMP.getSize()).append(", ");
            builder.append("(int[]) {").append(testTaskTMP.getExpectedValue()).append("}");
        } else if (testTaskTMP.getTask().getReturnType() == ReturnType.CHARVECTOR) {
            builder.append("(int*) ").append(testTaskTMP.getSize()).append(", ");
            builder.append("(char[]) {").append(testTaskTMP.getExpectedValue()).append("}");
        } else {
            builder.append(testTaskTMP.getExpectedValue());
        }
        builder.append(", ");
        builder.append("&testOverall, &testFailed);\n");
    }

    private static void generateArgumentsForTest(TestArgument testArgument, StringBuilder builder) {
        if (testArgument.getType() == ReturnType.CHAR) {
            builder.append("'\\''").append(testArgument.getArgument()).append("'\\''");
        } else if (testArgument.getType() == (ReturnType.INTVECTOR)) {
            builder.append("(int[]) {").append(testArgument.getArgument()).append("} ");
            builder.append(",(int*) ").append(testArgument.getSize());
        } else if (testArgument.getType() == ReturnType.CHARVECTOR || testArgument.getType() == ReturnType.STRING) {
            builder.append("(char[]) {").append(testArgument.getArgument()).append("} ");
            builder.append(",(int*) ").append(testArgument.getSize());
        } else {
            builder.append(testArgument.getArgument());
        }

        builder.append(", ");
    }


    // ----------------- TYPE SPECIFIERS -----------------
    private static String printfSpecifiers(ReturnType returnType) {
        return switch (returnType) {
            case INT, INTVECTOR, BOOLEAN -> "%d";
            case FLOAT -> "%f";
            case DOUBLE -> "%e";
            case CHAR, CHARVECTOR -> "%c";
            case STRING -> "%s";
        };
    }

    private static String getTypeToString(ReturnType returnType) {
        return switch (returnType) {
            case INT -> "int";
            case FLOAT -> "float";
            case DOUBLE -> "double";
            case BOOLEAN -> "bool";
            case CHAR -> "char";
            case STRING -> "char*";
            case INTVECTOR -> "int*";
            case CHARVECTOR -> "char*";
        };
    }

    private String getTypeToTestFunction(ReturnType returnType) {
        return switch (returnType) {
            case INT -> "int";
            case FLOAT -> "float";
            case DOUBLE -> "double";
            case BOOLEAN -> "bool";
            case CHAR -> "char";
            case STRING -> "(char[])";
            case INTVECTOR -> "(int[])";
            case CHARVECTOR -> "(char[])";
        };
    }


}
