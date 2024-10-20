package com.example.openCode.CompilationModule.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSolution {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length =  1024)
    private String solutionCode;

    private Long solvingTaskId;

    @OneToOne
    UserSolutionStatistics userSolutionStatistics;
}
