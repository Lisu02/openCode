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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length =  1024*2)
    private String description;

    @OneToOne
    Task task;
}
