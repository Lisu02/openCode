package com.example.openCode.CompilationModule.Model.Task;

import com.example.openCode.CompilationModule.Model.Task.TestTask.TestTask;
import com.example.openCode.CompilationModule.Model.Users.Users;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Task")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    public Task(long id, ReturnType returnType, String functionName, List<FunctionArgument> argumentList, List<TestTask> testTaskList) {
        this.id = id;
        this.returnType = returnType;
        this.functionName = functionName;
        this.argumentList = argumentList;
        this.testList = testTaskList;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "returnType")
    @Enumerated(EnumType.STRING)
    private ReturnType returnType;

    @Column(name = "funtionName")
    private String functionName;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FunctionArgument> argumentList;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestTask> testList;

    @ManyToOne
    Users user;

    @OneToOne
    @JoinColumn(name = "taskDescription_id")
    TaskDescription taskDescription;


    public String getCatalogName(){
        return getId() + "-" + getFunctionName();
    }
}
