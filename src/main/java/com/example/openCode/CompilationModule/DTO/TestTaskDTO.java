package com.example.openCode.CompilationModule.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TestTaskDTO {

    private Long id;
    private Long taskId;
    private List<TestInputArgumentDTO> testInputArgumentDTOList;
    private String expectedValue;
}
