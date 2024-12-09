package com.example.openCode.CompilationModule.Model.Task;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "TaskDescription")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDescription {
    @Id
    private Long id;

    @Column(length =  1024)
    private String description;

    @OneToOne
    Task task;
}
