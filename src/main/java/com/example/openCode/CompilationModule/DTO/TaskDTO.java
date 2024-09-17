package com.example.openCode.CompilationModule.DTO;
import lombok.*;
import java.util.List;

@Data
@Builder
public class TaskDTO {
    // returnType funtionName(argumentList);
    // int testName(int hello,string word);
    private long id;
    private String returnType;
    private String functionName;
    private List<FunctionArgumentDTO> argumentList;
    private List<TestTaskDTO> testList;
}
