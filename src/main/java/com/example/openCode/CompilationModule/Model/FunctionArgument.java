package com.example.openCode.CompilationModule.Model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FunctionArgument {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "type")
    private ReturnType type;

    @Column(name = "name")
    private String name;

    @ManyToOne()
    @JoinColumn(name = "taskId", nullable = false)
    private Task task;
}
