package com.example.openCode.CompilationModule.Service;

import com.example.openCode.CompilationModule.DTO.FunctionArgumentDTO;
import com.example.openCode.CompilationModule.DTO.TaskDTO;
import com.example.openCode.CompilationModule.Model.FunctionArgument;
import com.example.openCode.CompilationModule.Model.Task;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    public TaskDTO mapToDTO(Task task){
        return TaskDTO.builder()
                .id(task.getId())
                .returnType(task.getReturnType().toString())
                .functionName(task.getFunctionName())
                .argumentList(mapFunctionArgumentToDTO(task.getArgumentList()))
                .testList(null)
                .build();
    }

    public FunctionArgumentDTO mapFunctionArgumentToDTO(FunctionArgument functionArgument){
        return FunctionArgumentDTO.builder()
                .id(functionArgument.getId())
                .type(functionArgument.getType().toString())
                .name(functionArgument.getName())
                .task(functionArgument.getTask().getId())
                .build();
    }

    public List<FunctionArgumentDTO> mapFunctionArgumentToDTO(List<FunctionArgument> functionArgument){
        //TODO: return list of functionArguments in DTO format
        return null;
    }
}
