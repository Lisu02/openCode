package com.example.openCode.CompilationModule.Service;

import com.example.openCode.CompilationModule.DTO.FunctionArgumentDTO;
import com.example.openCode.CompilationModule.DTO.TaskDTO;
import com.example.openCode.CompilationModule.DTO.TestInputArgumentDTO;
import com.example.openCode.CompilationModule.DTO.TestTaskDTO;
import com.example.openCode.CompilationModule.Model.*;
import com.example.openCode.CompilationModule.Model.Task.FunctionArgument;
import com.example.openCode.CompilationModule.Model.Task.Task;
import com.example.openCode.CompilationModule.Model.TestTask.TestArguments;
import com.example.openCode.CompilationModule.Model.TestTask.TestTask;
import com.example.openCode.CompilationModule.Repository.TaskRepository;
import com.example.openCode.CompilationModule.Service.Exception.TaskNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

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

    public void saveTask(Task task){
        taskRepository.save(task);
    }


    //-------------------- MAPPING METHODS --------------------

    //MAPPING TASK the highest
    //MAPPING FUNCTION ARGUMENTS high
    //MAPPING TESTS TASKS lower
    //MAPPING TEST INPUT ARGUMENTS lowest

    //Task

    public TaskDTO mapTaskToDTO(Task task){
        return TaskDTO.builder()
                .id(task.getId())
                .returnType(task.getReturnType().toString())
                .functionName(task.getFunctionName())
                .argumentList(mapFunctionArgumentListToDTO(task.getArgumentList()))
                .testList(null)
                .build();
    }

    public Task mapTaskDTOtoTask(TaskDTO taskDTO){
        return Task.builder()
                .returnType(ReturnType.valueOf(taskDTO.getReturnType().toUpperCase()))
                .functionName(taskDTO.getFunctionName())
                .argumentList(mapFunctionArgumentListToDTO(taskDTO.getArgumentList()))
                .testList(taskDTO.getTestList())
                .build();
    }

    public List<TaskDTO> mapTaskListToTaskDTOList(List<Task> taskList){
        return taskList.stream().map(this::mapTaskToDTO).toList();
    }


    //FunctionArgument
    public FunctionArgumentDTO mapFunctionArgumentToDTO(FunctionArgument functionArgument){
        return FunctionArgumentDTO.builder()
                .id(functionArgument.getId())
                .type(functionArgument.getType().toString())
                .name(functionArgument.getName())
                .task(functionArgument.getTask().getId())
                .build();
    }

    public FunctionArgument mapFunctionArgumentDTOtoObject(FunctionArgumentDTO functionArgumentDTO){

        Optional<Task> taskOptional = taskRepository.findById(functionArgumentDTO.getTask());

        if(taskOptional.isPresent()){
            return FunctionArgument.builder()
                    .type(ReturnType.valueOf(functionArgumentDTO.getType()))
                    .name(functionArgumentDTO.getName())
                    .task(taskOptional.get())
                    .build();
        }else{
            throw new TaskNotFoundException("Task by id " + functionArgumentDTO.getTask() + " does not exits in database");
        }


    }

    public List<FunctionArgumentDTO> mapFunctionArgumentListToDTO(List<FunctionArgument> functionArgument){
        //TODO: return list of functionArguments in DTO format

        return null;
    }


    //TestTask
    public TestTaskDTO mapTestTaskToDTO(TestTask testTask){
        return TestTaskDTO.builder()
                .expectedValue(testTask.getExpectedValue())
                .testInputArgumentDTOList(mapTestInputArgumentListToDTO(testTask.getTestArguments()))
                .build();
    }

    public List<TestTaskDTO> mapTestTaskListToDTO(List<TestTask> testTaskList){
        return testTaskList.stream().map(this::mapTestTaskToDTO).toList();
    }


    //TestInputArgument (TestArguments) todo:rename arguments
    public List<TestInputArgumentDTO> mapTestInputArgumentListToDTO(List<TestArguments> testArgumentsList){
        return testArgumentsList.stream().map(this::mapTestInputArgumentToDTO).toList();
    }

    public TestInputArgumentDTO mapTestInputArgumentToDTO(TestArguments testArguments){
        return TestInputArgumentDTO.builder()
                .type(testArguments.getType().toString())
                .testArgument(testArguments.getTestArgument())
                .build();
    }
}
