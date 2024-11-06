package com.example.openCode.CompilationModule.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSolutionDTO {

    private Long solvingTaskId;

    private String programmingLanguage;
    private String solutionCode;

    //UserSolutionStatistics
    private Long runTime;
    private Long memoryUsage;
}
