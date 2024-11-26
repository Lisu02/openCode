package com.example.openCode.CompilationModule.Service.DockerHandler;

import com.example.openCode.CompilationModule.Model.Task.Task;
import com.example.openCode.CompilationModule.Model.UserSolution;
import com.example.openCode.CompilationModule.Model.UserSolutionStatistics;
import com.example.openCode.CompilationModule.Repository.UserSolutionRepository;
import com.example.openCode.CompilationModule.Repository.UserSolutionStatisticsRepository;
import org.springframework.stereotype.Component;

public abstract class DockerSolutionHandler {

    protected UserSolutionRepository userSolutionRepository;
    protected UserSolutionStatisticsRepository userSolutionStatisticsRepository;

    public DockerSolutionHandler(UserSolutionRepository userSolutionRepository, UserSolutionStatisticsRepository userSolutionStatisticsRepository) {
        this.userSolutionRepository = userSolutionRepository;
        this.userSolutionStatisticsRepository = userSolutionStatisticsRepository;
    }

    protected String processOutput(MyResultCallback callback, UserSolution userSolution) {
        String outputTime = DockerUtils.getTime(callback.getOutput());
        String outputMemory = DockerUtils.getMemory(callback.getOutput());

        UserSolutionStatistics statistics = UserSolutionStatistics.builder()
                .runTime(DockerUtils.convertStringTimeToLong(outputTime))
                .memoryUsage(DockerUtils.convertStringMemoryToLong(outputMemory))
                .build();

        statistics.setUserSolution(userSolution);
        userSolution.setUserSolutionStatistics(statistics);

        userSolutionStatisticsRepository.save(statistics);
        userSolutionRepository.save(userSolution);

        return DockerUtils.getOnlyCodeOutput(callback.getOutput());
    }

    public abstract String solveInDocker(UserSolution userSolution, Task task);


}
