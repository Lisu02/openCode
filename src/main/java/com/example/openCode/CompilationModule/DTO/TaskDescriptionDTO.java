package com.example.openCode.CompilationModule.DTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
public class TaskDescriptionDTO {
    private long taskId;
    private String description;
}
