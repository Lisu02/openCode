package com.example.openCode.CompilationModule.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSolution {

    @Id
    private Long id;

    private String solutionCode;

    private Long solvingTaskId;
}
