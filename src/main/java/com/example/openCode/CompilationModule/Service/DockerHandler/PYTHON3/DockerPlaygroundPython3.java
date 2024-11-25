package com.example.openCode.CompilationModule.Service.DockerHandler.PYTHON3;

import com.example.openCode.CompilationModule.Model.PlaygroundCode;
import com.example.openCode.CompilationModule.Service.DockerHandler.ContainerIdList;
import com.example.openCode.CompilationModule.Service.DockerHandler.ContainerStatus;
import com.example.openCode.CompilationModule.Service.DockerHandler.DockerConfiguration;
import com.example.openCode.CompilationModule.Service.DockerHandler.MyResultCallback;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DockerPlaygroundPython3 {

    private static final Logger log = LoggerFactory.getLogger(DockerPlaygroundPython3.class);
    private DockerClient dockerClient = DockerConfiguration.getDockerClientInstance();
    private String python3ContainerId = ContainerIdList.getPython3ContainerId();  // ID pythona nie gcc!!

    private void killDockerContainer() {
        dockerClient.killContainerCmd(python3ContainerId).exec();
    }


    public String execute(PlaygroundCode playgroundCode) {

        String sourceCode = playgroundCode.getCode();
        String fileName = playgroundCode.getId().toString() + ".py";
        String filePath = "/tmp/" + fileName;

        // Komenda do zapisania kodu do pliku w kontenerze
        String createFileCommand = "printf '%s' '".concat(sourceCode).concat("'").concat(" > /tmp/" + fileName);


        // Uruchamianie kontenera, jeśli jeszcze nie działa
        if (!ContainerStatus.isContainerRunning(python3ContainerId)) {
            dockerClient.startContainerCmd(python3ContainerId).exec();
        }

        // Utworzenie pliku z kodem użytkownika wewnątrz kontenera
        ExecCreateCmdResponse execCreateFile = dockerClient.execCreateCmd(python3ContainerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withCmd("sh", "-c", createFileCommand)
                .exec();

        MyResultCallback createFileCallback = new MyResultCallback();
        dockerClient.execStartCmd(execCreateFile.getId()).exec(createFileCallback);

        try {
            createFileCallback.awaitCompletion();
        } catch (InterruptedException e) {
            log.error("Error while creating file in container", e);
            return "File creation error";
        }

        // Uruchomienie pliku Python z kodem użytkownika
        ExecCreateCmdResponse execRunFile = dockerClient.execCreateCmd(python3ContainerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withCmd("time -v python3", filePath)  // poprawione formatowanie
                .exec();

        MyResultCallback runFileCallback = new MyResultCallback();
        dockerClient.execStartCmd(execRunFile.getId()).exec(runFileCallback);

        try {
            runFileCallback.awaitCompletion();
        } catch (InterruptedException e) {
            log.error("Error while running Python code in container", e);
            return "Execution error";
        }
        //killDockerContainer();
        log.info("Python Run Output: " + runFileCallback.getOutput());
        int stringLength = runFileCallback.getOutput().length();
        int outputTimeSize = runFileCallback.getOutput().lastIndexOf("Elapsed (wall clock) time (h:mm:ss or m:ss):");
        int outputMemorySize = runFileCallback.getOutput().lastIndexOf("Minor (reclaiming a frame) page faults:");
        int outputTimeStart = runFileCallback.getOutput().lastIndexOf("Command being timed:");
        String outputTime = runFileCallback.getOutput().substring(outputTimeSize, outputTimeSize + 7);
        String outputSize = runFileCallback.getOutput().substring(outputMemorySize,outputMemorySize + 7);
        System.out.println("OutputTime -> " + outputTime);
        System.out.println("OutputSize -> " + outputSize);
        return runFileCallback.getOutput().substring(0,stringLength - 20);
    }
}

