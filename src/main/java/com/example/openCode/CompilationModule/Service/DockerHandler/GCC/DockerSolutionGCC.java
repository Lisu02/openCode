package com.example.openCode.CompilationModule.Service.DockerHandler.GCC;

import com.example.openCode.CompilationModule.Model.Task.Task;
import com.example.openCode.CompilationModule.Model.UserSolution;
import com.example.openCode.CompilationModule.Service.DockerHandler.ContainerIdList;
import com.example.openCode.CompilationModule.Service.DockerHandler.DockerConfiguration;
import com.example.openCode.CompilationModule.Service.DockerHandler.MyResultCallback;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DockerSolutionGCC {

    //TODO:Dokończyć metody compile oraz run oraz poprawić generowanie testów (błąd z jednym enterem + blad z wczytywaniem)

    private static final Logger log = LoggerFactory.getLogger(DockerSolutionGCC.class);
    DockerClient dockerClient = DockerConfiguration.getDockerClientInstance();
    String gccContainerId = ContainerIdList.getGccContainerId();

    public String solveInDocker(UserSolution userSolution, Task task) {
        String taskCatalogName = task.getCatalogName(); // 1-mnozenie id-nazwafunkcji
        StringBuilder output = new StringBuilder();

        output.append(addUserSolutionToTaskCatalog(userSolution,taskCatalogName));
        if (!output.isEmpty()){
            log.atError().log("Adding user solution to a task catalog failed -> " + output);
            return "Adding user solution to a task catalog failed";
        }

        output.append(compile(taskCatalogName,userSolution.getId().toString()));
        if(!output.isEmpty()){
            log.atError().log("Compilation failure: \n" + output);
            return "Compilation failure\n" + output;
        }

        output.append(runCodeWithTests(taskCatalogName,userSolution.getId().toString()));
        return output.toString();
    }

    private String addUserSolutionToTaskCatalog(UserSolution userSolution, String taskCatalogName)  {
        ExecCreateCmdResponse addSolutionCodeToCatalog = dockerClient.execCreateCmd(gccContainerId)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withCmd("sh","-c"," echo \"" + userSolution.getSolutionCode() + "\" > /tmp/" + taskCatalogName + "/" + userSolution.getId() + ".c")
                .exec();
        MyResultCallback addSolutionCodeCallback = new MyResultCallback();
        dockerClient.execStartCmd(addSolutionCodeToCatalog.getId()).exec(addSolutionCodeCallback);
        try {
            addSolutionCodeCallback.awaitCompletion();
        }catch (InterruptedException e) {
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
                .withCmd("sh","-c","gcc -o " + operationPath + "/" + solutionFileName + " " //gcc -o /tmp/1-mnozenie/1
                        + operationPath + "/" + solutionFileName + ".c" + " " // /tmp/1-mnozenie/mnozenie.c
                        + operationPath + "/" + "test.c"  // /tmp/1-mnozenie/test.c
                )
                .exec();
        MyResultCallback compilationCallback = new MyResultCallback();

        dockerClient.execStartCmd(compilationCommand.getId()).exec(compilationCallback);

        try{
            compilationCallback.awaitCompletion();
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        return compilationCallback.getOutput();
    }

    private String runCodeWithTests(String taskCatalogName,String solutionFileName){
        ExecCreateCmdResponse runCommand = dockerClient.execCreateCmd(gccContainerId)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withCmd("sh","-c","./tmp/" + taskCatalogName + "/" + solutionFileName) // ./tmp/1-mnozenie/1
                .exec();
        MyResultCallback runCallback = new MyResultCallback();

        dockerClient.execStartCmd(runCommand.getId()).exec(runCallback);

        try{
            runCallback.awaitCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return runCallback.getOutput();
    }


}
