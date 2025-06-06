package com.example.openCode.CompilationModule.Model;

import com.example.openCode.CompilationModule.Model.Users.Users;
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

    private String programmingLanguage;

    @Column(length =  1024 * 2)
    private String solutionCode;

    private Long solvingTaskId;

    @OneToOne(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY,
            mappedBy = "userSolution"
    )
    private UserSolutionStatistics userSolutionStatistics;

    @ManyToOne
    Users user;
}
