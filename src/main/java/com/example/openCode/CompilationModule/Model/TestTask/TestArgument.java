package com.example.openCode.CompilationModule.Model.TestTask;

import com.example.openCode.CompilationModule.Model.ReturnType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class TestArgument {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ReturnType type;  //todo: Probably will be removed soon

    @Column(name = "testArgument")
    private String argument;

    @ManyToOne
    private TestTask testTask;
}
