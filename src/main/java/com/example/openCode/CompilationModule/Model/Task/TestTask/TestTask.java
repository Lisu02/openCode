package com.example.openCode.CompilationModule.Model.Task.TestTask;

import com.example.openCode.CompilationModule.Model.Task.Task;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class TestTask {


    public TestTask(long id, Task task, List<TestArgument> argumentList, String expectedValue) {
        this.id = id;
        this.task = task;
        this.testArguments = argumentList;
        this.expectedValue = expectedValue;
        this.size = -1;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private Task task;

    @OneToMany(mappedBy = "testTask", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestArgument> testArguments;

    @Column(name = "expectedValue")
    private String expectedValue;

    @Column(name = "size")
    private int size;
}
