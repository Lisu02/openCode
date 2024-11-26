package com.example.openCode.CompilationModule.Service.DockerHandler.GCC;

import com.example.openCode.CompilationModule.Model.Task.Task;
import com.example.openCode.CompilationModule.Model.UserSolution;
import com.example.openCode.CompilationModule.Repository.UserSolutionRepository;
import com.example.openCode.CompilationModule.Repository.UserSolutionStatisticsRepository;
import com.example.openCode.CompilationModule.Service.DockerHandler.*;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.example.openCode.CompilationModule.Service.DockerHandler.DockerUtils.*;

@Component
public class DockerSolutionGCC extends DockerSolutionHandler {

    //TODO:Dokończyć metody compile oraz run oraz poprawić generowanie testów (błąd z jednym enterem + blad z wczytywaniem)

    private static final Logger log = LoggerFactory.getLogger(DockerSolutionGCC.class);
    private DockerClient dockerClient = DockerConfiguration.getDockerClientInstance();
    private String gccContainerId = ContainerIdList.getGccContainerId();

    private UserSolutionRepository userSolutionRepository;
    private UserSolutionStatisticsRepository userSolutionStatisticsRepository;

    @Autowired
    public DockerSolutionGCC(UserSolutionRepository userSolutionRepository, UserSolutionStatisticsRepository userSolutionStatisticsRepository) {
        super(userSolutionRepository, userSolutionStatisticsRepository);
    }

    public String solveInDocker(UserSolution userSolution, Task task) {
        String taskCatalogName = task.getCatalogName(); // 1-mnozenie id-nazwafunkcji
        StringBuilder output = new StringBuilder();

        output.append(addUserSolutionToTaskCatalog(userSolution, taskCatalogName));
        if (!output.isEmpty()) {
            log.atError().log("Adding user solution to a task catalog failed -> " + output);
            return "Adding user solution to a task catalog failed";
        }

        output.append(compile(taskCatalogName, userSolution.getId().toString()));
        if (!output.isEmpty()) {
            log.atError().log("Compilation failure: \n" + output);
            return "Compilation failure\n" + output;
        }

        output.append(runCodeWithTests(taskCatalogName, userSolution.getId().toString(), userSolution));
        return output.toString();
    }

    protected String addUserSolutionToTaskCatalog(UserSolution userSolution, String taskCatalogName) {

        //String createFileCommand = "echo  $'".concat(userSolution.getSolutionCode()).concat("'").concat(" > /tmp/" + taskCatalogName +"/" + userSolution.getId() +".c");
        //TODO:dopasować do testowania zadan ta komendę

        ExecCreateCmdResponse addSolutionCodeToCatalog = dockerClient.execCreateCmd(gccContainerId)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withCmd("sh", "-c", " printf '%s' '" + userSolution.getSolutionCode() + "' > /tmp/" + taskCatalogName + "/" + userSolution.getId() + ".c")
                .exec();
        MyResultCallback addSolutionCodeCallback = new MyResultCallback();
        dockerClient.execStartCmd(addSolutionCodeToCatalog.getId()).exec(addSolutionCodeCallback);
        try {
            addSolutionCodeCallback.awaitCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return addSolutionCodeCallback.getOutput();
    }

    private String compile(String taskCatalogName, String solutionFileName) {

        String operationPath = "/tmp/" + taskCatalogName; // #tmp/1-mnozenie

        //Nazwa pliku po kompilacji -> Kod użytkownika.c -> Testy.c
        ExecCreateCmdResponse compilationCommand = dockerClient.execCreateCmd(gccContainerId)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withCmd("sh", "-c", "gcc -O2 -o " + operationPath + "/" + solutionFileName + " " //gcc -o /tmp/1-mnozenie/1
                        + operationPath + "/" + solutionFileName + ".c" + " " // /tmp/1-mnozenie/mnozenie.c
                        + operationPath + "/" + "test.c"  // /tmp/1-mnozenie/test.c
                )
                .exec();
        MyResultCallback compilationCallback = new MyResultCallback();

        dockerClient.execStartCmd(compilationCommand.getId()).exec(compilationCallback);

        try {
            compilationCallback.awaitCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return compilationCallback.getOutput();
    }

    protected String runCodeWithTests(String taskCatalogName, String solutionFileName, UserSolution userSolution) {
        ExecCreateCmdResponse runCommand = dockerClient.execCreateCmd(gccContainerId)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withCmd("sh", "-c", "time -v ./tmp/" + taskCatalogName + "/" + solutionFileName) // ./tmp/1-mnozenie/1
                .exec();
        MyResultCallback runCallback = new MyResultCallback();

        dockerClient.execStartCmd(runCommand.getId()).exec(runCallback);
        boolean isTimedOut = false;

        try {
            isTimedOut = !runCallback.awaitCompletion(DockerUtils.basicTimeoutTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!isTimedOut) {
            return processOutput(runCallback,userSolution);
        }
        return "Execution error: C code took too long to execute";
    }
}
