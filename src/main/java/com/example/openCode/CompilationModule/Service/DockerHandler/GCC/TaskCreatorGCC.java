package com.example.openCode.CompilationModule.Service.DockerHandler.GCC;

import com.example.openCode.CompilationModule.Model.ReturnType;
import com.example.openCode.CompilationModule.Model.Task.FunctionArgument;
import com.example.openCode.CompilationModule.Model.Task.Task;
import com.example.openCode.CompilationModule.Model.TestTask.TestArgument;
import com.example.openCode.CompilationModule.Model.TestTask.TestTask;
import com.example.openCode.CompilationModule.Service.DockerHandler.ContainerIdList;
import com.example.openCode.CompilationModule.Service.DockerHandler.ContainerStatus;
import com.example.openCode.CompilationModule.Service.DockerHandler.DockerConfiguration;
import com.example.openCode.CompilationModule.Service.DockerHandler.MyResultCallback;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Formatter;
import java.util.Iterator;

@Component
public class TaskCreatorGCC {

    //This class role is to create code tasks inside a docker container that can
    //be run to check if the solution of the user is correct

    private static final Logger log = LoggerFactory.getLogger(TaskCreatorGCC.class);
    DockerClient dockerClient = DockerConfiguration.getDockerClientInstance();
    String gccContainerId = ContainerIdList.getGccContainerId();


    public void createTaskInContainer(Task task){
        if(isTaskReadyForCreation(task)){
            String catalogName = task.getId() + "-" + task.getFunctionName();
            createTaskFiles(task,catalogName);
            StringBuilder testCodeForUser = new StringBuilder();
            StringBuilder testCodeForTests = new StringBuilder();
            generateTaskCodeForUser(task,testCodeForUser,true);
            generateTaskCodeForTests(task,testCodeForTests,catalogName);


            ExecCreateCmdResponse createTaskDocker = dockerClient.execCreateCmd(gccContainerId)
                    .withAttachStderr(true)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .withCmd("sh", "-c",
                            "echo \"" + testCodeForTests.toString().replace("\"", "\\\"") + "\" > tmp/" + catalogName + "/test.c && " +
                                    "echo \"" + testCodeForUser.toString().replace("\"", "\\\"") + "\" > tmp/" + catalogName + "/" + task.getFunctionName() + ".c"
                    )
                    .exec();
            MyResultCallback callback = new MyResultCallback();
            dockerClient.execStartCmd(createTaskDocker.getId()).exec(callback);
            try {
                callback.awaitCompletion();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            System.out.println(callback.getOutput());
        }
    }

    public boolean isTaskCreatedinDockerContainer(Task task){
       // dockerClient
        return false;
    }


    private void createTaskFiles(Task task, String catalog){
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
        }catch (InterruptedException e ){
            e.printStackTrace();
        }
        System.out.println(createTaskFilesCallback.getOutput());
    }

    private void generateTaskCodeForUser(Task task,StringBuilder builder,boolean withBrackets){
        //StringBuilder builder = new StringBuilder();
        builder.append(task.getReturnType().toString().toLowerCase()); //RETURN TYPE
        builder.append(" ");
        builder.append(task.getFunctionName());
        builder.append("(");
        Iterator<FunctionArgument> iterator = task.getArgumentList().iterator();
        FunctionArgument functionArgumentTMP;
        while(iterator.hasNext()){
            functionArgumentTMP = iterator.next();
            builder.append(functionArgumentTMP.getType().toString().toLowerCase()).append(" ").append(functionArgumentTMP.getName());
            if(iterator.hasNext()){builder.append(", ");}
        }
        if(withBrackets){builder.append(")\n{\n}");}
        else{builder.append(");\n");}
    }
    private void generateTaskCodeForTests(Task task,StringBuilder builder,String catalog){
        //StringBuilder builder = new StringBuilder();
        //todo: dodac wyciaganie danych z taska zeby za kazdym razem nie uzywac iteratora jakos czytelniej??
        builder.append("#include <stdio.h>\n");
        generateTaskCodeForUser(task,builder,false);

        builder.append("#define OPERATION \"");
        builder.append(task.getFunctionName());
        builder.append("\"\n");

        generateTestFunctionToDocker(task, builder);

        generateTestMainFunction(task, builder);
    }

    private static void generateTestMainFunction(Task task, StringBuilder builder) {
        builder.append("int main(){\n");
        builder.append("\tint testOverall = 0;\n");
        builder.append("\tint testFailed = 0;\n");

        Iterator<TestTask> testIterator = task.getTestList().iterator();
        TestTask testTaskTMP;
        Iterator<TestArgument> testArgumentIterator;
        TestArgument testArgument;

        while(testIterator.hasNext()){
            testTaskTMP = testIterator.next();
            builder.append("\ttest(");
            builder.append(task.getFunctionName());
            builder.append(", ");
            testArgumentIterator = testTaskTMP.getTestArguments().iterator();
            while(testArgumentIterator.hasNext()){
                testArgument = testArgumentIterator.next();
                builder.append(testArgument.getArgument());
                builder.append(", ");
            }
            builder.append(testTaskTMP.getExpectedValue());
            builder.append(", ");
            builder.append("&testOverall, &testFailed);\n");
        }

        builder.append("\tprintf(\"\\n overall: %d, failed %d \", testOverall, testFailed);\n");

        builder.append("\treturn 0;\n");

        builder.append("}");
    }
    private void generateTestFunctionToDocker(Task task, StringBuilder builder) {
        builder.append("void test(");
        builder.append(task.getReturnType().toString().toLowerCase());
        builder.append(" (*operation)(");
        Iterator<FunctionArgument> iterator = task.getArgumentList().iterator();
        FunctionArgument functionArgumentTMP;
        while(iterator.hasNext()){
            functionArgumentTMP = iterator.next();
            builder.append(functionArgumentTMP.getType().toString().toLowerCase());
            if(iterator.hasNext()){
                builder.append(",");}
        }
        iterator = task.getArgumentList().iterator();
        builder.append("),");
        while (iterator.hasNext()){
            functionArgumentTMP = iterator.next();
            builder.append(functionArgumentTMP.getType().toString().toLowerCase());
            builder.append(" ");
            builder.append(functionArgumentTMP.getName());
            builder.append(","); //Lack of hasNext check is on purpose
        }
        builder.append(task.getReturnType().toString().toLowerCase());
        builder.append(" expected,int* overall,int* failed){\n"); //space is necessary

        builder.append("\t");
        builder.append(task.getReturnType().toString().toLowerCase());
        builder.append(" result = operation(");
        iterator = task.getArgumentList().iterator();
        while (iterator.hasNext()){
            functionArgumentTMP = iterator.next();
            builder.append(functionArgumentTMP.getName());
            if(iterator.hasNext()){
                builder.append(",");}
        }
        builder.append(");\n");

        builder.append("\t*overall = *overall + 1;\n");

        builder.append("\tif(result == expected) {\n");

        builder.append("\t\tprintf(\"Test passed: %s(");
        iterator = task.getArgumentList().iterator();
        while (iterator.hasNext()){
            functionArgumentTMP = iterator.next();
            builder.append(printfSpecifiers(functionArgumentTMP.getType()));
            if(iterator.hasNext()){
                builder.append(", ");}
        }
        builder.append(") == ");
        builder.append(printfSpecifiers(task.getReturnType()));
        builder.append("\",OPERATION,");
        iterator = task.getArgumentList().iterator();
        while (iterator.hasNext()){
            functionArgumentTMP = iterator.next();
            builder.append(functionArgumentTMP.getName());
            builder.append(",");
        }
        builder.append("expected);\n");

        builder.append("\t} else {\n");

        builder.append("\t\tprintf(\"Test failed: %s(");
        iterator = task.getArgumentList().iterator();
        while (iterator.hasNext()){
            functionArgumentTMP = iterator.next();
            builder.append(printfSpecifiers(functionArgumentTMP.getType()));
            if(iterator.hasNext()){
                builder.append(", ");}
        }
        builder.append(") == ");
        builder.append(printfSpecifiers(task.getReturnType()));
        builder.append(", got ");
        builder.append(printfSpecifiers(task.getReturnType()));
        builder.append(" instead\",OPERATION,");
        iterator = task.getArgumentList().iterator();
        while (iterator.hasNext()){
            functionArgumentTMP = iterator.next();
            builder.append(functionArgumentTMP.getName());
            builder.append(", ");
        }
        builder.append("expected, result);\n");

        builder.append("\t\t*failed = *failed + 1;\n");

        builder.append("\t}\n");

        builder.append("}\n");
    }


    private String printfSpecifiers(ReturnType returnType){
        return switch (returnType){
            case INT, INTMATRIX, INTVECTOR, BOOLEAN -> "%d";
            case FLOAT -> "%f";
            case DOUBLE -> "%e";
            case CHAR, CHARMATRIX, CHARVECTOR -> "%c";
            case STRING -> "%s";
        };
    }
    private boolean isTaskReadyForCreation(Task task){
        if(task.getArgumentList() == null || task.getTestList() == null){
            log.warn("Task: {}is not ready for creation inside a GCC container", task.getId());
            return false;
        }
        Iterator<TestTask> iterator = task.getTestList().iterator();
        TestTask testTaskTmp;
        while(iterator.hasNext()){
            testTaskTmp = iterator.next();
            if(testTaskTmp.getTestArguments() == null || testTaskTmp.getExpectedValue().isBlank()){
                log.warn("Task: {}is not ready for creation inside a GCC container", task.getId());
                return false;
            }
        }
        return true;
    }



}
