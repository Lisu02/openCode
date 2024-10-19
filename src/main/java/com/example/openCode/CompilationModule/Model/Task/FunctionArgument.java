package com.example.openCode.CompilationModule.Model.Task;


import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class FunctionArgument {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ReturnType type;

    @Column(name = "name")
    private String name;

    @ManyToOne()
    @JoinColumn(name = "taskId", nullable = false)
    private Task task;
}
