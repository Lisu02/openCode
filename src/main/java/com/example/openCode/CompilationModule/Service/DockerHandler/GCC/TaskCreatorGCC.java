package com.example.openCode.CompilationModule.Service.DockerHandler.GCC;

import com.example.openCode.CompilationModule.Model.ReturnType;
import com.example.openCode.CompilationModule.Model.Task.FunctionArgument;
import com.example.openCode.CompilationModule.Model.Task.Task;
import com.example.openCode.CompilationModule.Model.TestTask.TestTask;
import com.example.openCode.CompilationModule.Service.DockerHandler.ContainerIdList;
import com.example.openCode.CompilationModule.Service.DockerHandler.ContainerStatus;
import com.example.openCode.CompilationModule.Service.DockerHandler.DockerConfiguration;
import com.example.openCode.CompilationModule.Service.DockerHandler.MyResultCallback;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Formatter;
import java.util.Iterator;

public class TaskCreatorGCC {

    //This class role is to create code tasks inside a docker container that can
    //be run to check if the solution of the user is correct

    private static final Logger log = LoggerFactory.getLogger(TaskCreatorGCC.class);
    DockerClient dockerClient = DockerConfiguration.getDockerClientInstance();
    String gccContainerId = ContainerIdList.getGccContainerId();


    private void createTaskFiles(Task task, String catalog){
        ExecCreateCmdResponse execCreateTaskFiles = dockerClient.execCreateCmd(gccContainerId)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withCmd("mkdir", "tmp/" + catalog + " &&" +
                        "rm tmp/"+catalog + "/*" + " &&"
                        + "touch" +
                        " tmp/"+catalog + "/" + "test.c" +
                        " tmp/"+catalog + "/" + task.getFunctionName() + ".c"
                )
                .exec();
        MyResultCallback createTaskFilesCallback = new MyResultCallback();
        dockerClient.execStartCmd(execCreateTaskFiles.getId()).exec(createTaskFilesCallback);
        try {
            createTaskFilesCallback.awaitCompletion();
        }catch (InterruptedException e ){
            e.printStackTrace();
        }
    }

    public void createTaskInContainer(Task task){
        if(isTaskReadyForCreation(task)){
            String catalogName = task.getId() + "-" + task.getFunctionName();
            createTaskFiles(task,catalogName);
            String codeForUser = generateTaskCodeForUser(task,true);
            String codeForTests = generateTaskCodeForTests(task,catalogName);
        }
    }

    private String generateTaskCodeForUser(Task task,boolean withBrackets){
        StringBuilder builder = new StringBuilder();
        builder.append(task.getReturnType().toString().toLowerCase()); //RETURN TYPE
        builder.append(" ");
        builder.append(task.getFunctionName());
        builder.append("(");
        Iterator<FunctionArgument> iterator = task.getArgumentList().iterator();
        FunctionArgument functionArgumentTMP;
        while(iterator.hasNext()){
            functionArgumentTMP = iterator.next();
            builder.append(functionArgumentTMP.getType()).append(" ").append(functionArgumentTMP.getName());
            if(iterator.hasNext()){builder.append(", ");}
        }
        builder.append(");\n");
        if(withBrackets){builder.append("{\n}");}
        return builder.toString();
    }
    private String generateTaskCodeForTests(Task task,String catalog){
        StringBuilder builder = new StringBuilder();
        builder.append("#include <stdio.h>\n");

        builder.append(generateTaskCodeForUser(task,false));

        builder.append("#define OPERATION \"");
        builder.append(task.getFunctionName());
        builder.append("\"\n");

        builder.append("void test(");
        builder.append(task.getReturnType().toString().toLowerCase());
        builder.append(" (*operation)(");
        Iterator<FunctionArgument> iterator = task.getArgumentList().iterator();
        FunctionArgument functionArgumentTMP;
        while(iterator.hasNext()){
            functionArgumentTMP = iterator.next();
            builder.append(functionArgumentTMP.getType().toString().toLowerCase());
            if(iterator.hasNext()){builder.append(",");}
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
        builder.append("int* overall,int* failed){\n");

        builder.append("\t");
        builder.append(task.getReturnType().toString().toLowerCase());
        builder.append(" result = operation(");
        iterator = task.getArgumentList().iterator();
        while (iterator.hasNext()){
            functionArgumentTMP = iterator.next();
            builder.append(functionArgumentTMP.getName());
            if(iterator.hasNext()){builder.append(",");}
        }
        builder.append(");\n");

        builder.append("\t*overall = *overall + 1;\n");

        builder.append("\tif(result == expected) {\n");

        builder.append("\tprintf(\"Test passed: %s(");
        iterator = task.getArgumentList().iterator();
        while (iterator.hasNext()){
            functionArgumentTMP = iterator.next();
            builder.append(printfSpecifiers(functionArgumentTMP.getType()));
            if(iterator.hasNext()){builder.append(", ");}
        }
        builder.append(") == ");
        builder.append(printfSpecifiers(task.getReturnType()));
        builder.append("\n\",OPERATION,");
        iterator = task.getArgumentList().iterator();
        while (iterator.hasNext()){
            functionArgumentTMP = iterator.next();
            builder.append(functionArgumentTMP.getName());
            if(iterator.hasNext()){builder.append(",");}
        }
        builder.append(");\n");

        builder.append("\t} else {\n");

        //todo:Dokonczyc generowanie pliku z testami
        return null;
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
