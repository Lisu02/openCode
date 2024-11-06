package com.example.openCode.CompilationModule.Service;

import com.example.openCode.CompilationModule.Model.Task.Task;
import com.example.openCode.CompilationModule.Model.UserSolution;
import com.example.openCode.CompilationModule.Repository.UserSolutionRepository;
import com.example.openCode.CompilationModule.Service.DockerHandler.DockerTaskLanguage;
import com.example.openCode.CompilationModule.Service.DockerHandler.GCC.DockerSolutionGCC;
import com.example.openCode.CompilationModule.Service.DockerHandler.GCC.DockerTaskGCC;
import com.example.openCode.CompilationModule.Service.DockerHandler.PYTHON3.DockerSolutionPython3;
import com.example.openCode.CompilationModule.Service.DockerHandler.PYTHON3.DockerTaskPython3;
import com.example.openCode.CompilationModule.Service.Task.TaskService;
import org.antlr.v4.runtime.misc.DoubleKeyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SolutionService {

    private final DockerTaskPython3 dockerTaskPython3;
    private final DockerSolutionPython3 dockerSolutionPython3;
    DockerSolutionGCC dockerSolutionGCC;
    DockerTaskGCC dockerTaskGCC;
    TaskService taskService;
    UserSolutionRepository userSolutionRepository;

    @Autowired
    public SolutionService(DockerSolutionGCC dockerSolutionGCC, DockerTaskGCC dockerTaskGCC, TaskService taskService, UserSolutionRepository userSolutionRepository, DockerTaskPython3 dockerTaskPython3, DockerSolutionPython3 dockerSolutionPython3) {
        this.dockerSolutionGCC = dockerSolutionGCC;
        this.dockerTaskGCC =  dockerTaskGCC;
        this.taskService = taskService;
        this.userSolutionRepository = userSolutionRepository;
        this.dockerTaskPython3 = dockerTaskPython3;
        this.dockerSolutionPython3 = dockerSolutionPython3;
    }

    public String solve(UserSolution userSolution) {
        return switch (userSolution.getProgrammingLanguage().toUpperCase()) {
            case "C" -> solveGCC(userSolution);
            case "PYTHON3" -> solvePython3(userSolution);
            default -> "SELECTED LANGUAGE NOT SUPPORTED";
        };
    }

    //TODO: Implement DRY!!!

    private String solveGCC(UserSolution userSolution) {
        Task task = taskService.getTaskById(userSolution.getSolvingTaskId());

        if(task != null){
            if(dockerTaskGCC.isTaskCreatedInDockerContainer(task)){
                userSolutionRepository.save(userSolution);
                String outpunt = dockerSolutionGCC.solveInDocker(userSolution, task);
                return outpunt;
            }else {
                return "Task is not created in Docker container";
            }
        }
        return "Task does not exist";
    }

    private String solvePython3(UserSolution userSolution){
        Task task = taskService.getTaskById(userSolution.getSolvingTaskId());

        if(task != null){
            if(dockerTaskPython3.isTaskCreatedInDockerContainer(task)){
                userSolutionRepository.save(userSolution);
                return dockerSolutionPython3.solveInDockerPython3(userSolution, task);
            }else {
                return "Task is not created in Docker container";
            }
        }
        return "Task does not exist";
    }
}
