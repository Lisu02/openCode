package com.example.openCode.CompilationModule.Model.Users;

import com.example.openCode.CompilationModule.Model.Task.Task;
import com.example.openCode.CompilationModule.Model.UserSolution;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

@Entity
@Getter
@Setter
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(unique = true)
    private String username;
    private String password;


    @OneToMany(mappedBy = "user")
    List<UserSolution> userSolutions;

    @OneToMany(mappedBy = "user")
    List<Task> taskList;
}
