package com.example.openCode.CompilationModule.Model.Task.TestTask;

import com.example.openCode.CompilationModule.Model.Task.ReturnType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class TestArgument {

    public TestArgument(long id,ReturnType type,String argument, TestTask testTask) {
        this.id = id;
        this.type = type;
        this.argument = argument;
        this.testTask = testTask;
        this.size = -1;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ReturnType type;  //todo: Probably will be removed soon

    @Column(name = "testArgument")
    private String argument;

    @Column(name = "size")
    private int size;

    @ManyToOne
    private TestTask testTask;
}
