package com.example.openCode.CompilationModule.Service.DockerHandler.PYTHON3;

import com.example.openCode.CompilationModule.Model.Task.Task;
import com.example.openCode.CompilationModule.Model.UserSolution;
import com.example.openCode.CompilationModule.Service.DockerHandler.ContainerIdList;
import com.example.openCode.CompilationModule.Service.DockerHandler.DockerConfiguration;
import com.example.openCode.CompilationModule.Service.DockerHandler.GCC.DockerSolutionGCC;
import com.example.openCode.CompilationModule.Service.DockerHandler.MyResultCallback;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DockerSolutionPython3 {

    private static final Logger log = LoggerFactory.getLogger(DockerSolutionPython3.class);
    private DockerClient dockerClient = DockerConfiguration.getDockerClientInstance();
    private String python3ContainerId = ContainerIdList.getPython3ContainerId();

    public String solveInDockerPython3(UserSolution userSolution, Task task) {
        addUserSolutionToTaskCatalog(userSolution, task);
        return runCode(userSolution, task);
    }


    private void addUserSolutionToTaskCatalog(UserSolution userSolution, Task task) {
        String userSolutionFilePath = "/tmp/" + task.getId() + "-" + task.getFunctionName() + "/" + userSolution.getId() + ".py";
        // /tmp/" + task.getId() + "-" + task.getFunctionName() + "/" + userSolution.getId()
        ExecCreateCmdResponse addSolutionCommand = dockerClient.execCreateCmd(python3ContainerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withCmd("sh","-c","touch " + userSolutionFilePath + " && " +
                        "printf '%s ' '" + userSolution.getSolutionCode() + "' > " + userSolutionFilePath  //todo: automatycznie sie tworzy ale moze byc problem z / przy tmp
                        )
                .exec();
        MyResultCallback addSolutionCallback = new MyResultCallback();
        dockerClient.execStartCmd(addSolutionCommand.getId()).exec(addSolutionCallback);

        try{
            addSolutionCallback.awaitCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info("addUserSolutionToTaskCatalog finished output: {}", addSolutionCallback.getOutput());
    }

    private String runCode(UserSolution userSolution,Task task) {

        String solutionFileName = userSolution.getId() + ".py";
        // ./tmp/" + taskCatalogName + "/" + solutionFileName
        ExecCreateCmdResponse runCommand = dockerClient.execCreateCmd(python3ContainerId)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withCmd("sh","-c"," python tmp/" + task.getCatalogName() + "/test.py " + "tmp/" + task.getCatalogName() + "/" + solutionFileName)
                .exec();
        MyResultCallback runCallback = new MyResultCallback();

        dockerClient.execStartCmd(runCommand.getId()).exec(runCallback);

        try{
            runCallback.awaitCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("runCode for Python output: {}",runCallback.getOutput());
        return runCallback.getOutput();
    }

}
