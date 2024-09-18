package com.example.openCode.CompilationModule.Service;

import com.example.openCode.CompilationModule.DTO.FunctionArgumentDTO;
import com.example.openCode.CompilationModule.DTO.TaskDTO;
import com.example.openCode.CompilationModule.Model.FunctionArgument;
import com.example.openCode.CompilationModule.Model.ReturnType;
import com.example.openCode.CompilationModule.Model.Task;
import com.example.openCode.CompilationModule.Repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskService {

    TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    public List<TaskDTO> getTaskDTOList(){
        List<Task> taskList = taskRepository.findAll();
        return mapTaskListToTaskDTOList(taskList);
    }

    public void saveTaskDTO(TaskDTO taskDTO){
        taskRepository.save(mapTaskDTOtoTask(taskDTO));
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<TaskDTO> getAllTasksDTO(){
        List<Task> taskList = taskRepository.findAll();
        return mapTaskListToTaskDTOList(taskList);
    }


    //-------------------- MAPPING METHODS --------------------

    public TaskDTO mapTaskToDTO(Task task){
        return TaskDTO.builder()
                .id(task.getId())
                .returnType(task.getReturnType().toString())
                .functionName(task.getFunctionName())
                .argumentList(mapFunctionArgumentToDTO(task.getArgumentList()))
                .testList(null)
                .build();
    }

    public Task mapTaskDTOtoTask(TaskDTO taskDTO){
        return Task.builder()
                .returnType(ReturnType.valueOf(taskDTO.getReturnType().toUpperCase()))
                .functionName(taskDTO.getFunctionName())
                .argumentList(taskDTO.getArgumentList())
                .testList(taskDTO.getTestList())
                .build();
    }

    public List<TaskDTO> mapTaskListToTaskDTOList(List<Task> taskList){
        return taskList.stream().map(this::mapTaskToDTO).toList();
    }

    public FunctionArgumentDTO mapFunctionArgumentToDTO(FunctionArgument functionArgument){
        return FunctionArgumentDTO.builder()
                .id(functionArgument.getId())
                .type(functionArgument.getType().toString())
                .name(functionArgument.getName())
                .task(functionArgument.getTask().getId())
                .build();
    }

    public List<FunctionArgumentDTO> mapFunctionArgumentListToDTO(List<FunctionArgument> functionArgument){
        //TODO: return list of functionArguments in DTO format
        return null;
    }
}
