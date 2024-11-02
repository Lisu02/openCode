package com.example.openCode.CompilationModule.Service.DockerHandler.PYTHON3;

import com.example.openCode.CompilationModule.Model.PlaygroundCode;
import com.example.openCode.CompilationModule.Service.DockerHandler.ContainerIdList;
import com.example.openCode.CompilationModule.Service.DockerHandler.ContainerStatus;
import com.example.openCode.CompilationModule.Service.DockerHandler.DockerConfiguration;
import com.example.openCode.CompilationModule.Service.DockerHandler.GCC.DockerPlaygroundGCC;
import com.example.openCode.CompilationModule.Service.DockerHandler.MyResultCallback;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DockerPlaygroundPython3 {

    private static final Logger log = LoggerFactory.getLogger(DockerPlaygroundPython3.class);
    DockerClient dockerClient = DockerConfiguration.getDockerClientInstance();
    String python3ContainerId = ContainerIdList.getGccContainerId();


    public String compile(PlaygroundCode playgroundCode) {

        String sourceCode = playgroundCode.getCode();
        String catalogName = playgroundCode.getId().toString();

        String createFileCommand = "printf '%s' '".concat(sourceCode).concat("'").concat(" > /tmp/" + catalogName + ".py");
        String runCodeCommand = "python " + "/tmp/" + playgroundCode.getId().toString() + ".py";

        if (!ContainerStatus.isContainerRunning(python3ContainerId)){
            dockerClient.startContainerCmd(python3ContainerId).exec();
        }
        System.out.println(createFileCommand);
        ExecCreateCmdResponse execAddFile = dockerClient.execCreateCmd(python3ContainerId)
                .withAttachStdout(true)
                .withAttachStdin(true)
                .withAttachStderr(true)
                .withCmd("sh","-c",createFileCommand)
                .exec();
        MyResultCallback callback = new MyResultCallback();
        dockerClient.execStartCmd(execAddFile.getId()).exec(callback);
        try{
            callback.awaitCompletion();
        }catch (InterruptedException e){
            e.printStackTrace();
            log.atError().log("InterruptedException");
        }
        System.out.println(callback.getOutput());
        ExecCreateCmdResponse execRunFile = dockerClient.execCreateCmd(python3ContainerId)
                .withAttachStdout(true)
                .withAttachStdin(true)
                .withAttachStderr(true)
                .withCmd(runCodeCommand)
                .exec();
        MyResultCallback runCallback = new MyResultCallback();
        dockerClient.execStartCmd(execRunFile.getId()).exec(runCallback);
        try{
            runCallback.awaitCompletion();
        }catch (InterruptedException e){
            e.printStackTrace();
            log.atError().log("InterruptedException");
        }

        log.atInfo().log("Python Run: " + sourceCode);
        return callback.getOutput() + runCallback.getOutput();
    }


}
