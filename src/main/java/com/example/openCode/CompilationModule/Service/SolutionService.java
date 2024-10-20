package com.example.openCode.CompilationModule.Service;

import com.example.openCode.CompilationModule.Model.Task.Task;
import com.example.openCode.CompilationModule.Model.UserSolution;
import com.example.openCode.CompilationModule.Repository.UserSolutionRepository;
import com.example.openCode.CompilationModule.Service.DockerHandler.GCC.DockerSolutionGCC;
import com.example.openCode.CompilationModule.Service.DockerHandler.GCC.DockerTaskGCC;
import com.example.openCode.CompilationModule.Service.Task.TaskService;
import org.antlr.v4.runtime.misc.DoubleKeyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SolutionService {

    DockerSolutionGCC dockerSolutionGCC;
    DockerTaskGCC dockerTaskGCC;
    TaskService taskService;
    UserSolutionRepository userSolutionRepository;

    @Autowired
    public SolutionService(DockerSolutionGCC dockerSolutionGCC, DockerTaskGCC dockerTaskGCC, TaskService taskService, UserSolutionRepository userSolutionRepository) {
        this.dockerSolutionGCC = dockerSolutionGCC;
        this.dockerTaskGCC =  dockerTaskGCC;
        this.taskService = taskService;
        this.userSolutionRepository = userSolutionRepository;
    }

    public String solve(UserSolution userSolution) {
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
}
