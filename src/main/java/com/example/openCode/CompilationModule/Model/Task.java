package com.example.openCode.CompilationModule.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Task")
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "returnType")
    private ReturnType returnType;

    @Column(name = "funtionName")
    private String functionName;

    @OneToMany(mappedBy = "task")
    private List<FunctionArgument> argumentList;

    @OneToMany(mappedBy = "task")
    private List<TestTask> testList;

}
