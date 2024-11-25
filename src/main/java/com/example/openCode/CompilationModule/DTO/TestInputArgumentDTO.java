package com.example.openCode.CompilationModule.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestInputArgumentDTO {

    private Long id;
    private String type;
    private String testArgument;
    private Long testTaskId;
    private int size;
}
