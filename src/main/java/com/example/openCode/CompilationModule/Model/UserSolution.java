package com.example.openCode.CompilationModule.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSolution {

    @Id
    private Long id;

    private String solutionCode;

    private Long solvingTaskId;

    @OneToOne
    UserSolutionStatistics userSolutionStatistics;
}
