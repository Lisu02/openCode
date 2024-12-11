package com.example.openCode.CompilationModule.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FunctionArgumentDTO {

    private long id;
    private String type;
    private String name;
    private int size;
    private long task;
}
