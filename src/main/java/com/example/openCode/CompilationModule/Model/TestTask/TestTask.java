package com.example.openCode.CompilationModule.Model.TestTask;

import com.example.openCode.CompilationModule.Model.Task.Task;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class TestTask {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private Task task;

    @OneToMany(mappedBy = "testTask")
    private List<TestArgument> testArguments;

    private String expectedValue;
}
