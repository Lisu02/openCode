package com.example.openCode.CompilationModule.Service.Task;

import com.example.openCode.CompilationModule.DTO.FunctionArgumentDTO;
import com.example.openCode.CompilationModule.DTO.TaskDTO;
import com.example.openCode.CompilationModule.DTO.TestInputArgumentDTO;
import com.example.openCode.CompilationModule.DTO.TestTaskDTO;
import com.example.openCode.CompilationModule.Model.Task.ReturnType;
import com.example.openCode.CompilationModule.Model.Task.FunctionArgument;
import com.example.openCode.CompilationModule.Model.Task.Task;
import com.example.openCode.CompilationModule.Model.Task.TestTask.TestArgument;
import com.example.openCode.CompilationModule.Model.Task.TestTask.TestTask;
import com.example.openCode.CompilationModule.Repository.TaskRepository;
import com.example.openCode.CompilationModule.Repository.TestTaskRepository;
import com.example.openCode.CompilationModule.Service.Exception.TaskNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public  class TaskMapper {

    TaskRepository taskRepository;
    TestTaskRepository testTaskRepository;

    public TaskMapper(TaskRepository taskRepository,TestTaskRepository testTaskRepository){
        this.taskRepository = taskRepository;
        this.testTaskRepository = testTaskRepository;
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
                .testList(mapTestTaskListToDTO(task.getTestList()))
                .build();
    }

    public Task mapTaskDTOtoTask(TaskDTO taskDTO){
        return Task.builder()
                .returnType(ReturnType.valueOf(taskDTO.getReturnType().toUpperCase()))
                .functionName(taskDTO.getFunctionName())
                .argumentList(mapFunctionArgumentDTOListToObject(taskDTO.getArgumentList()))
                .testList(mapTestTaskDTOListToObject(taskDTO.getTestList()))
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
        return functionArgument.stream().map(argument -> mapFunctionArgumentToDTO(argument)).toList();
    }

    public List<FunctionArgument> mapFunctionArgumentDTOListToObject(List<FunctionArgumentDTO> functionArgumentDTOList){
        return functionArgumentDTOList.stream()
                .map(this::mapFunctionArgumentDTOtoObject)
                .collect(Collectors.toList());
    }


    //TestTask
    public TestTaskDTO mapTestTaskToDTO(TestTask testTask){
        return TestTaskDTO.builder()
                .id(testTask.getId())
                .taskId(testTask.getTask().getId())
                .expectedValue(testTask.getExpectedValue())
                .testInputArgumentDTOList(mapTestInputArgumentListToDTO(testTask.getTestArguments()))
                .build();
    }

    public TestTask mapTestTaskDTOtoObject(TestTaskDTO testTaskDTO){

        Optional<Task> task = taskRepository.findById(testTaskDTO.getTaskId());
        if(task.isEmpty()){
            return null;
        }
        return TestTask.builder()
                .testArguments(mapTestInputArgumentDTOListToObject(testTaskDTO.getTestInputArgumentDTOList()))
                .expectedValue(testTaskDTO.getExpectedValue())
                .task(task.get())
                .build();
    }

    public List<TestTaskDTO> mapTestTaskListToDTO(List<TestTask> testTaskList){
        return testTaskList.stream().map(this::mapTestTaskToDTO).toList();
    }

    public List<TestTask> mapTestTaskDTOListToObject(List<TestTaskDTO> testListDTO) {
        return testListDTO.stream().map(this::mapTestTaskDTOtoObject).collect(Collectors.toList());
    }


    //TestInputArgument (TestArguments) todo:rename arguments
    public TestInputArgumentDTO mapTestInputArgumentToDTO(TestArgument testArgument){
        return TestInputArgumentDTO.builder()
                .id(testArgument.getId())
                .type(testArgument.getType().toString())
                .testArgument(testArgument.getArgument())
                .testTaskId(testArgument.getTestTask().getId())
                .build();
    }

    public TestArgument mapTestInputArgumentDTOtoObject(TestInputArgumentDTO testInputArgumentDTO){
        Optional<TestTask> testTask = testTaskRepository.findById(testInputArgumentDTO.getTestTaskId());
        if(testTask.isEmpty()){
            return null;
        }

        return TestArgument.builder()
                .type(ReturnType.valueOf(testInputArgumentDTO.getType().toUpperCase()))
                .argument(testInputArgumentDTO.getTestArgument())
                .testTask(testTask.get())
                .build();
    }

    public List<TestInputArgumentDTO> mapTestInputArgumentListToDTO(List<TestArgument> testArgumentList){
        return testArgumentList.stream().map(this::mapTestInputArgumentToDTO).toList();
    }

    private List<TestArgument> mapTestInputArgumentDTOListToObject(List<TestInputArgumentDTO> testInputArgumentDTOList) {
        return testInputArgumentDTOList.stream()
                .map(this::mapTestInputArgumentDTOtoObject)
                .collect(Collectors.toList());
    }

}
