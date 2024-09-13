package com.example.openCode.CompilationModule.Prototype;

import com.example.openCode.CompilationModule.Model.UserCode;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class DockerCompilatorGCC {

    private static final Logger log = LoggerFactory.getLogger(DockerCompilatorGCC.class);
    DockerClient dockerClient = DockerConfiguration.getDockerClientInstance();
    //TODO:Sprawdzanie id kontenera po liscie
    String gccContainerId = ContainerIdList.getGccContainerId();

    //private String compileComand = "gcc -o /tmp/hello /tmp/hello.c 2>&1 >/tmp/compile_output.txt";

    private boolean isContainerRunning() {
        InspectContainerResponse inspectResponse = dockerClient.inspectContainerCmd(gccContainerId).exec();
        return Boolean.TRUE.equals(inspectResponse.getState().getRunning());
    }

    public String compile(UserCode userCode) {

        String sourceCode = userCode.getUserCode();
        String catalogName = userCode.getId().toString();

        String compileComand = "gcc -o /tmp/" + catalogName + " /tmp/" + catalogName + ".c";


        if (!isContainerRunning()) {
            dockerClient.startContainerCmd(gccContainerId).exec();
            //log.atInfo().log("Starting a GCC container: " + gccContainerId);
        } else {
            //log.atInfo().log("GCC Container is running no need for starting");
        }

        //System.out.println(dockerClient.listContainersCmd().exec());
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
    public String runCode(String catalogName) throws InterruptedException {

        if (!isContainerRunning()) {
            dockerClient.startContainerCmd(gccContainerId).exec();
            //log.atInfo().log("Starting a GCC container: " + gccContainerId);
        } else {
            //log.atInfo().log("GCC Container is running no need for starting");
        }

        ExecCreateCmdResponse execRun = dockerClient.execCreateCmd(gccContainerId)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withCmd("/tmp/./" + catalogName)
                .exec();

        MyResultCallback callbackRun = new MyResultCallback();

        dockerClient.execStartCmd(execRun.getId()).exec(callbackRun);
        //TODO:Ustawic timeouty dla zadan
        callbackRun.awaitCompletion(1500, TimeUnit.MILLISECONDS);
        String output = callbackRun.getOutput();
        log.atInfo().log("Runnable output -> \n" + output);

        ExecCreateCmdResponse execDelete = dockerClient.execCreateCmd(gccContainerId)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withCmd("rm", "/tmp/" + catalogName, "&&", "rm", "/tmp/" + catalogName + ".c")
                .exec();
        MyResultCallback callbackDelete = new MyResultCallback();
        //dockerClient.execStartCmd(execDelete.getId()).exec(callbackDelete);
        //callbackDelete.awaitCompletion(100,TimeUnit.MILLISECONDS);
        return output;
    }

    //Overloaded method
    public String runCode(UserCode userCode) {
        String output = "empty";
        try {
            output = runCode(userCode.getId().toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
            dockerClient.stopContainerCmd(gccContainerId);
            output = "ERROR possibly timeout";
        }
        return output;
    }


}
