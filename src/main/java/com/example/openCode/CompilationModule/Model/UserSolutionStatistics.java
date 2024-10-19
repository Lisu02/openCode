package com.example.openCode.CompilationModule.Model;

import jakarta.persistence.Column;
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
public class UserSolutionStatistics {

    @Id
    private Long id;

    @Column(name = "runTime")
    private Long runTime;

    @Column(name = "memoryUsage")
    private Long memoryUsage;

    @OneToOne
    UserSolution userSolution;
}
