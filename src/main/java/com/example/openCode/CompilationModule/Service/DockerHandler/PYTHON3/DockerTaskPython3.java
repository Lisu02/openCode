package com.example.openCode.CompilationModule.Service.DockerHandler.PYTHON3;

import com.example.openCode.CompilationModule.Model.Task.FunctionArgument;
import com.example.openCode.CompilationModule.Model.Task.ReturnType;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;

@Component
public class DockerTaskPython3 implements DockerTaskLanguage {

    private static final Logger log = LoggerFactory.getLogger(DockerTaskPython3.class);
    private DockerClient dockerClient = DockerConfiguration.getDockerClientInstance();
    private String python3ContainerId = ContainerIdList.getPython3ContainerId();

    public void createTaskInContainer(Task task){
        if(TaskService.isTaskReadyForCreation(task) && createDirectoryForTask(task)){
            StringBuilder builder = new StringBuilder();

            generatePythonTestScript(task, builder);

            String catalogName = task.getCatalogName();
            String fileName = task.getFunctionName() + ".py";

            ExecCreateCmdResponse createTaskDockerPython = dockerClient.execCreateCmd(python3ContainerId)
                    .withAttachStdout(true)
                    .withAttachStdin(true)
                    .withAttachStderr(true)
                    .withCmd("sh","-c", "printf '%s' '" + builder + "' > /tmp/" + task.getCatalogName() + "/test.py")
                    .exec();

            MyResultCallback createTaskCallback = new MyResultCallback();

            dockerClient.execStartCmd(createTaskDockerPython.getId()).exec(createTaskCallback);

            try{
                createTaskCallback.awaitCompletion();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            log.info("Creating task in Python3 ->{}", createTaskCallback.getOutput());
        }
    }

    public Boolean isTaskCreatedInDockerContainer(Task task){
        return true; //todo: dokończyć pod pythona3
    }


    private boolean createDirectoryForTask(Task task){
        ExecCreateCmdResponse execCreateDirectory = dockerClient.execCreateCmd(python3ContainerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withCmd("sh","-c","mkdir -p tmp/" + task.getCatalogName() + " && " +
                        "rm -f tmp/" + task.getCatalogName() +"/* && " +
                        "touch tmp/" + task.getId() + "-" + task.getFunctionName() + "/test.py"
                )
                .exec();
        MyResultCallback createDirectoryCallback = new MyResultCallback();
        dockerClient.execStartCmd(execCreateDirectory.getId()).exec(createDirectoryCallback);
        try{
            createDirectoryCallback.awaitCompletion();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(createDirectoryCallback.getOutput().isEmpty()){
            return true;
        }else {
            log.warn("Unsuccessful creation of a task directory {}, Output: {}",task.getId(),createDirectoryCallback.getOutput());
            return false;
        }
    }

    private void generatePythonTestScriptOLD(Task task, StringBuilder builder) {
        builder.append("import importlib.util\n");
        builder.append("import sys\n\n");

        builder.append("OPERATION = \"").append(task.getFunctionName()).append("\"\n\n");

        builder.append("def load_user_function(file_path):\n");
        builder.append("    spec = importlib.util.spec_from_file_location(\"user_module\", file_path)\n");
        builder.append("    user_module = importlib.util.module_from_spec(spec)\n");
        builder.append("    spec.loader.exec_module(user_module)\n");
        builder.append("    return getattr(user_module, \"").append(task.getFunctionName()).append("\")\n\n");

        builder.append("def test(operation, liczba, mnoznik, expected, overall, failed):\n");
        builder.append("    result = operation(liczba, mnoznik)\n");
        builder.append("    overall += 1\n");
        builder.append("    if result == expected:\n");
        builder.append("        print(f\"Test passed: {OPERATION}({liczba}, {mnoznik}) == {expected}\")\n");
        builder.append("    else:\n");
        builder.append("        print(f\"Test failed: {OPERATION}({liczba}, {mnoznik}) == {expected}, got {result} instead\")\n");
        builder.append("        failed += 1\n");
        builder.append("    return overall, failed\n\n");

        builder.append("def main(user_file):\n");
        builder.append("    mnozenie = load_user_function(user_file)\n\n");
        builder.append("    overall = 0\n");
        builder.append("    failed = 0\n\n");

        // Generate test cases based on the task object
        builder.append("    # Run tests on the user-submitted function\n");

        for (TestTask testTask : task.getTestList()) {
            builder.append("    overall, failed = test(mnozenie, ");
            Iterator<TestArgument> testArgumentIterator = testTask.getTestArguments().iterator();
            while (testArgumentIterator.hasNext()) {
                TestArgument testArgument = testArgumentIterator.next();
                builder.append(testArgument.getArgument());
                if (testArgumentIterator.hasNext()) builder.append(", ");
            }
            builder.append(", ").append(testTask.getExpectedValue()).append(", overall, failed)\n");
        }

        builder.append("\n    print(f\"overall: {overall}, failed: {failed}\")\n\n");

        builder.append("if __name__ == \"__main__\":\n");
        builder.append("    if len(sys.argv) != 2:\n");
        builder.append("        print(\"Usage: python test_script.py <user_code_file.py>\")\n");
        builder.append("        sys.exit(1)\n\n");
        builder.append("    user_file = sys.argv[1]\n");
        builder.append("    main(user_file)\n");
    }

    //TODO: 2 RODZAJE ZWRACANIA BŁĘDÓW 1 TO EXIT 2 TO RAISE
    private void generatePythonTestScript(Task task,StringBuilder builder) {
        builder.append("import importlib.util\n");
        builder.append("import argparse\n\n");
        builder.append("FUNCTION_NAME = \"").append(task.getFunctionName()).append("\"\n\n");

        builder.append("def load_module_from_file(file_path):\n");
        builder.append("\tspec = importlib.util.spec_from_file_location(\"user_solution\", file_path)\n");
        builder.append("\tuser_module = importlib.util.module_from_spec(spec)\n");
        builder.append("\tspec.loader.exec_module(user_module)\n");
        builder.append("\treturn user_module\n\n\n");

        builder.append("def test(operation");
        for (FunctionArgument functionArgument : task.getArgumentList()) {
            builder.append(",").append(functionArgument.getName());
        }
        builder.append(", expected, overall, failed):\n");
        builder.append("\ttry:\n");
        builder.append("\t\tresult = operation(");
        Iterator<FunctionArgument> funArgIterator = task.getArgumentList().iterator();
        while (funArgIterator.hasNext()) {
            builder.append(funArgIterator.next().getName());
            if (funArgIterator.hasNext()) builder.append(", ");
        }
        builder.append(")\n");
        builder.append("\t\tif result == expected:\n");
        builder.append("\t\t\toverall[0] += 1\n");
        builder.append("\t\telse:\n");
        builder.append("\t\t\toverall[0] += 1\n");
        builder.append("\t\t\tfailed[0] += 1\n");
        builder.append("\t\t\tprint(f\"Test failed: {operation.__name__}(");
        funArgIterator = task.getArgumentList().iterator();
        while (funArgIterator.hasNext()) {
            builder.append("{").append(funArgIterator.next().getName()).append("}");
            if (funArgIterator.hasNext()) builder.append(", ");
        }
        builder.append(") = {result}, expected {expected}\")\n");
        builder.append("\texcept Exception as e:\n");
        builder.append("\t\toverall[0] += 1\n");
        builder.append("\t\tfailed[0] += 1\n");
        builder.append("\t\tprint(f\"Test failed with exception: {e}\")\n");
        builder.append("\t\texit()\n\n\n");


        builder.append("def main():\n");
        builder.append("\tparser = argparse.ArgumentParser()\n");
        builder.append("\tparser.add_argument(\"solution_file\");\n");
        builder.append("\targs = parser.parse_args()\n\n");

        builder.append("\tuser_module = load_module_from_file(args.solution_file)\n\n");

        builder.append("\toperation = getattr(user_module, FUNCTION_NAME)\n\n");


        //Lista testów
        builder.append("\toverall = [0]\n");
        builder.append("\tfailed = [0]\n");
        builder.append("\ttest_cases = [\n");
        for(TestTask testTask : task.getTestList()) {
            builder.append("\t\t(");
            for(TestArgument testArgument: testTask.getTestArguments()) {
                writeProperArgument(testArgument.getArgument(), testArgument.getType(),builder);
                builder.append(", ");
            }
            writeProperArgument(testTask.getExpectedValue(), task.getReturnType(), builder);
            builder.append("),\n");
        }
        builder.append("\t]\n\n");

        //Wywoływanie funkcji test z argumentami
        builder.append("\tfor ");//for number, expected in test_cases:
        for(FunctionArgument functionArgument : task.getArgumentList()) {
            builder.append(functionArgument.getName()).append(", ");
        }
        builder.append("expected in test_cases:\n");

        builder.append("\t\ttest(operation, ");
        for(FunctionArgument functionArgument : task.getArgumentList()) {
            builder.append(functionArgument.getName()).append(", ");
        }
        builder.append("expected, overall, failed)\n");

        builder.append("\tprint(f\"Overall tests: {overall[0]}\")\n");
        builder.append("\tprint(f\"Failed tests: {failed[0]}\")\n\n");

        builder.append("if __name__ == \"__main__\":\n");
        builder.append("\tmain()");

    }

    private void writeProperArgumentForTestCase(TestArgument testArgument, StringBuilder builder) {
        switch (testArgument.getType()){
            case INT,DOUBLE,FLOAT -> builder.append(testArgument.getArgument());
            case CHAR -> builder.append("'").append(testArgument.getArgument()).append("'"); // 'A'
            case STRING -> builder.append("\"").append(testArgument.getArgument()).append("\""); // "slowo"
            case INTVECTOR -> builder.append("[").append(testArgument.getArgument()).append("]"); // [1,2,3]
            case CHARVECTOR -> builder.append("[").append(testArgument.getArgument()).append("]"); // jest [a,b,c]
            case BOOLEAN -> builder.append(testArgument.getArgument().substring(0, 1).toUpperCase());
            //a powinno byc ['a','b','c']?? //todo: sprawdzić w c czy charvector na pewno działa
        }
    }

    private void writeProperArgument(String argument, ReturnType returnType, StringBuilder builder) {
        switch (returnType){
            case INT,DOUBLE,FLOAT -> builder.append(argument);
            case CHAR -> builder.append("'").append(argument).append("'"); // 'A'
            case STRING -> builder.append("\"").append(argument).append("\""); // "slowo"
            case INTVECTOR -> builder.append("[").append(argument).append("]"); // [1,2,3]
            case CHARVECTOR -> builder.append("[").append(argument).append("]"); // jest [a,b,c]
            case BOOLEAN -> builder.append(argument.substring(0, 1).toUpperCase() + argument.substring(1));
        }
    }

    private String getTypeToFunction(ReturnType returnType) {
        return switch (returnType) {
            case INT -> "int";
            case FLOAT -> "float";
            case DOUBLE -> "float";
            case BOOLEAN -> "bool";
            case CHAR -> "str";
            case STRING -> "str";
            case INTVECTOR -> "list[int]"; //from typing import List, Union dla laczenia typow
            case CHARVECTOR -> "list[str]";
        };
    }


    public void getTaskCodeForUser(Task task, Map<String, String> response) {
        StringBuilder builder = new StringBuilder();
        builder.append("def ").append(task.getFunctionName()).append("(");
        Iterator<FunctionArgument> fnArgIterator = task.getArgumentList().iterator();
        while(fnArgIterator.hasNext()) {
            FunctionArgument fnArg = fnArgIterator.next();
            builder.append(fnArg.getName()).append(": ").append(getTypeToFunction(fnArg.getType()));
            if(fnArgIterator.hasNext()){builder.append(", ");}
        }
        builder.append("):\n");
        builder.append("\t");
        response.put("codeForUserPYTHON3",builder.toString());
    }
}
