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
        String taskCatalogName = task.getCatalogName();
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

    private String addUserSolutionToTaskCatalog(UserSolution userSolution, String taskCatalogName) {
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
        ExecCreateCmdResponse compilationCommand = dockerClient.execCreateCmd(gccContainerId)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withCmd("sh","-c")
                .exec();
        MyResultCallback compilationCallback = new MyResultCallback();
        //"gcc -c ./tmp/" + taskCatalogName + "/" + solutionFileName + ""

        return "";
    }

    private String runCodeWithTests(String taskCatalogName,String solutionFileName){
        ExecCreateCmdResponse runCommand = dockerClient.execCreateCmd(gccContainerId)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withCmd("sh","-c")
                .exec();
        MyResultCallback runCallback = new MyResultCallback();
        return "";
    }


}
