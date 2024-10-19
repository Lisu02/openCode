package com.example.openCode.CompilationModule.Service.DockerHandler.GCC;

import com.example.openCode.CompilationModule.Model.PlaygroundCode;
import com.example.openCode.CompilationModule.Service.DockerHandler.ContainerIdList;
import com.example.openCode.CompilationModule.Service.DockerHandler.ContainerStatus;
import com.example.openCode.CompilationModule.Service.DockerHandler.DockerConfiguration;
import com.example.openCode.CompilationModule.Service.DockerHandler.MyResultCallback;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class DockerPlaygroundGCC {

    private static final Logger log = LoggerFactory.getLogger(DockerPlaygroundGCC.class);
    DockerClient dockerClient = DockerConfiguration.getDockerClientInstance();
    String gccContainerId = ContainerIdList.getGccContainerId();

    //private String compileComand = "gcc -o /tmp/hello /tmp/hello.c 2>&1 >/tmp/compile_output.txt";

    private boolean isContainerRunning() {
        InspectContainerResponse inspectResponse = dockerClient.inspectContainerCmd(gccContainerId).exec();
        return Boolean.TRUE.equals(inspectResponse.getState().getRunning());
    }





    //-----------OpenCode Playground------------

    public String compile(PlaygroundCode playgroundCode) {

        String sourceCode = playgroundCode.getCode();
        String catalogName = playgroundCode.getId().toString();

        String compileComand = "gcc -o /tmp/" + catalogName + " /tmp/" + catalogName + ".c";


        if (!ContainerStatus.isContainerRunning(gccContainerId)) {
            dockerClient.startContainerCmd(gccContainerId).exec();
            }

        //System.out.println(dockerClient.listContainersCmd().exec());
        System.out.println(sourceCode);
        ExecCreateCmdResponse execCompileUserCode = dockerClient.execCreateCmd(gccContainerId)
                .withAttachStdout(true)
                .withAttachStdin(true)
                .withAttachStderr(true)
                .withCmd("sh", "-c", "echo \"" + sourceCode + "\" > /tmp/" + catalogName + ".c && " + compileComand)
                .exec();
        MyResultCallback callbackCompile = new MyResultCallback();
        dockerClient.execStartCmd(execCompileUserCode.getId()).exec(callbackCompile);
        try {
            callbackCompile.awaitCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.atError().log("COMPILATION FAILED!");
        }

        log.atInfo().log("Compilation output -> " + callbackCompile.getOutput());
        // dockerClient.stopContainerCmd(gccContainerId).exec();
        return callbackCompile.getOutput();
    }

    //TODO: add catalogName for userCode recognition
    public String runCode(String codeFileName) throws InterruptedException {

        if (!ContainerStatus.isContainerRunning(gccContainerId)) {
            dockerClient.startContainerCmd(gccContainerId).exec();
        }

        ExecCreateCmdResponse execRun = dockerClient.execCreateCmd(gccContainerId)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withCmd("/tmp/./" + codeFileName)
                .exec();

        MyResultCallback callbackRun = new MyResultCallback();

        dockerClient.execStartCmd(execRun.getId()).exec(callbackRun);
        //TODO:Ustawic timeouty dla zadan
        callbackRun.awaitCompletion(1500,TimeUnit.MILLISECONDS);
        String output = callbackRun.getOutput();
        log.atInfo().log("Runnable output -> \n" + output);

        ExecCreateCmdResponse execDelete = dockerClient.execCreateCmd(gccContainerId)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withCmd("rm", "/tmp/" + codeFileName, "&&", "rm", "/tmp/" + codeFileName + ".c")
                .exec();
        MyResultCallback callbackDelete = new MyResultCallback();
        //dockerClient.execStartCmd(execDelete.getId()).exec(callbackDelete);
        //callbackDelete.awaitCompletion(100,TimeUnit.MILLISECONDS);
        return output;
    }

    //Overloaded method
    public String runCode(PlaygroundCode playgroundCode) {
        String output = "empty";
        try {
            output = runCode(playgroundCode.getId().toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
            dockerClient.stopContainerCmd(gccContainerId);
            output = "ERROR possibly timeout";
        }
        return output;
    }


}
