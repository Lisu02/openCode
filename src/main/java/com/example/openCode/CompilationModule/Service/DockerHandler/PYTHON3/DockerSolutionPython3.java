package com.example.openCode.CompilationModule.Service.DockerHandler.PYTHON3;

import com.example.openCode.CompilationModule.Model.Task.Task;
import com.example.openCode.CompilationModule.Model.UserSolution;
import com.example.openCode.CompilationModule.Model.UserSolutionStatistics;
import com.example.openCode.CompilationModule.Repository.UserSolutionRepository;
import com.example.openCode.CompilationModule.Repository.UserSolutionStatisticsRepository;
import com.example.openCode.CompilationModule.Service.DockerHandler.ContainerIdList;
import com.example.openCode.CompilationModule.Service.DockerHandler.DockerConfiguration;
import com.example.openCode.CompilationModule.Service.DockerHandler.DockerSolutionHandler;
import com.example.openCode.CompilationModule.Service.DockerHandler.GCC.DockerSolutionGCC;
import com.example.openCode.CompilationModule.Service.DockerHandler.MyResultCallback;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.example.openCode.CompilationModule.Service.DockerHandler.DockerUtils.*;

@Component
public class DockerSolutionPython3 extends DockerSolutionHandler {

    private static final Logger log = LoggerFactory.getLogger(DockerSolutionPython3.class);
    private DockerClient dockerClient = DockerConfiguration.getDockerClientInstance();
    private String python3ContainerId = ContainerIdList.getPython3ContainerId();

    @Autowired
    public DockerSolutionPython3(UserSolutionRepository userSolutionRepository, UserSolutionStatisticsRepository userSolutionStatisticsRepository) {
        super(userSolutionRepository, userSolutionStatisticsRepository);
    }

    public String solveInDocker(UserSolution userSolution, Task task) {
        addUserSolutionToTaskCatalog(userSolution, task);
        return runCodeWithTests(userSolution, task);
    }

    protected String addUserSolutionToTaskCatalog(UserSolution userSolution, Task task) {
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
        return addSolutionCallback.getOutput();
    }

    protected String runCodeWithTests(UserSolution userSolution,Task task) {

        String solutionFileName = userSolution.getId() + ".py";
        // ./tmp/" + taskCatalogName + "/" + solutionFileName
        ExecCreateCmdResponse runCommand = dockerClient.execCreateCmd(python3ContainerId)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withCmd("sh","-c","time -v python3 tmp/" + task.getCatalogName() + "/test.py " + "tmp/" + task.getCatalogName() + "/" + solutionFileName)
                .exec();
        MyResultCallback runCallback = new MyResultCallback();

        dockerClient.execStartCmd(runCommand.getId()).exec(runCallback);
        boolean isTimedOut = false;
        try{
            isTimedOut = !runCallback.awaitCompletion(basicTimeoutTime, TimeUnit.MILLISECONDS);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info("runCode for Python output: {}",runCallback.getOutput());

        if(!isTimedOut){
            return processOutput(runCallback,userSolution);
        }


        return "Execution error: Python code took too long to execute";
    }

}
