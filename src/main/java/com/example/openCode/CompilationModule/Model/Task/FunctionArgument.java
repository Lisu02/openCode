package com.example.openCode.CompilationModule.Model.Task;


import com.example.openCode.CompilationModule.DTO.FunctionArgumentDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class FunctionArgument {
    public FunctionArgument(long id,ReturnType type, String name, Task task){
        this.id = id;
        this.type = type;
        this.name = name;
        size = 0;
        this.task = task;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ReturnType type;

    @Column(name = "name")
    private String name;

    @Column(name = "size")
    private int size = 0;

    @ManyToOne()
    @JoinColumn(name = "taskId", nullable = false)
    private Task task;
}
