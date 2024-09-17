package com.example.openCode.CompilationModule.DTO;

import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class TestArgumentsDTO {

    private Long id;
    private String type;
    private String testArgument;
    //private Long testTaskId;
    //TODO: delete testTaskId
}
