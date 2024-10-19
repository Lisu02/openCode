package com.example.openCode.CompilationModule.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSolutionDTO {

    private String solutionCode;
    private Long solvingTaskId;

    //UserSolutionStatistics
    private Long runTime;
    private Long memoryUsage;
}
