package com.example.openCode.CompilationModule.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSolutionStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "runTime")
    private Long runTime; //ms

    @Column(name = "memoryUsage")
    private Long memoryUsage; //kbytes ??

    @OneToOne(mappedBy = "userSolutionStatistics", cascade = CascadeType.ALL)
    UserSolution userSolution;
}
