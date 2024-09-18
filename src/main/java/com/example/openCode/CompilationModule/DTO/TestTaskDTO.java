package com.example.openCode.CompilationModule.DTO;

import lombok.Data;

import java.util.List;

@Data
public class TestTaskDTO {

    private Long id;
    private Long taskId;
    private List<TestInputArgumentDTO> testInputArgumentDTOList;
    private String expectedValue;
}
