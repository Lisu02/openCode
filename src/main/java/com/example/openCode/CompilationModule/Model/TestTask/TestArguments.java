package com.example.openCode.CompilationModule.Model.TestTask;

import com.example.openCode.CompilationModule.Model.ReturnType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TestArguments {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "type")
    private ReturnType type;

    @Column(name = "testArgument")
    private String testArgument;

    @ManyToOne
    private TestTask testTask;
}
