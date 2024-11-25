package com.example.openCode.CompilationModule.Service.DockerHandler.PYTHON3;

import com.example.openCode.CompilationModule.Model.PlaygroundCode;
import com.example.openCode.CompilationModule.Model.UserSolutionStatistics;
import com.example.openCode.CompilationModule.Service.DockerHandler.ContainerIdList;
import com.example.openCode.CompilationModule.Service.DockerHandler.ContainerStatus;
import com.example.openCode.CompilationModule.Service.DockerHandler.DockerConfiguration;
import com.example.openCode.CompilationModule.Service.DockerHandler.MyResultCallback;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.example.openCode.CompilationModule.Service.DockerHandler.DockerUtils.*;

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
                .withCmd("sh", "-c", "time -v python3 " + filePath)// poprawione formatowanie
                .exec();

        MyResultCallback runFileCallback = new MyResultCallback();
        dockerClient.execStartCmd(execRunFile.getId()).exec(runFileCallback);

        boolean isTimedOut;
        try {
            isTimedOut = !runFileCallback.awaitCompletion(basicTimeoutTime, TimeUnit.MILLISECONDS);

        } catch (InterruptedException e) {
            log.error("Error while running Python code in container", e);
            return "Execution error";
        }
        //killDockerContainer();
        System.out.println(isTimedOut);
        log.info("Python Run Output: " + runFileCallback.getOutput());
        if(!isTimedOut){
            String outputTime = getTime(runFileCallback.getOutput());
            String outputMemory = getMemory(runFileCallback.getOutput());

            log.info("PLAYGROUND: Output time -> " + outputTime + " | Memory -> " + outputMemory);
            return getOnlyCodeOutput(runFileCallback.getOutput());
        }

        return "Execution error: Python code took too long to execute";
    }
}

