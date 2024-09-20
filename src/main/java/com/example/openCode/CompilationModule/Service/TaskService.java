package com.example.openCode.CompilationModule.Service;

import com.example.openCode.CompilationModule.DTO.FunctionArgumentDTO;
import com.example.openCode.CompilationModule.DTO.TaskDTO;
import com.example.openCode.CompilationModule.DTO.TestInputArgumentDTO;
import com.example.openCode.CompilationModule.DTO.TestTaskDTO;
import com.example.openCode.CompilationModule.Model.*;
import com.example.openCode.CompilationModule.Model.Task.FunctionArgument;
import com.example.openCode.CompilationModule.Model.Task.Task;
import com.example.openCode.CompilationModule.Model.TestTask.TestArgument;
import com.example.openCode.CompilationModule.Model.TestTask.TestTask;
import com.example.openCode.CompilationModule.Repository.FunctionArgumentRepository;
import com.example.openCode.CompilationModule.Repository.TaskRepository;
import com.example.openCode.CompilationModule.Repository.TestArgumentRepository;
import com.example.openCode.CompilationModule.Repository.TestTaskRepository;
import com.example.openCode.CompilationModule.Service.Exception.TaskNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {

    TaskRepository taskRepository;
    FunctionArgumentRepository functionArgumentRepository;
    TestTaskRepository testTaskRepository;
    TestArgumentRepository testArgumentRepository;


    @Autowired
    public TaskService(TaskRepository taskRepository,
                       FunctionArgumentRepository functionArgumentRepository,
                       TestTaskRepository testTaskRepository,
                       TestArgumentRepository testArgumentRepository
    ){
        this.taskRepository = taskRepository;
        this.functionArgumentRepository = functionArgumentRepository;
        this.testTaskRepository = testTaskRepository;
        this.testArgumentRepository = testArgumentRepository;
    }

    //-------------GETTING TASKS-------------

    public List<TaskDTO> getTaskDTOList(){
        List<Task> taskList = taskRepository.findAll();
        return mapTaskListToTaskDTOList(taskList);
    }


    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<TaskDTO> getAllTasksDTO(){
        List<Task> taskList = taskRepository.findAll();
        return mapTaskListToTaskDTOList(taskList);
    }

    public TaskDTO getTaskDTObyId(long id){
        Optional<Task> taskDTO = taskRepository.findById(id);
        if(taskDTO.isEmpty()) return null;
        return mapTaskToDTO(taskDTO.get());
    }

    //-----------SAVING TASK---------------

    public void saveTaskDTO(TaskDTO taskDTO){
        saveTask(mapTaskDTOtoTask(taskDTO));
    }

    public void saveTask(Task task){
        taskRepository.save(task);
        saveFunctionArgument(task.getArgumentList());
        saveTestTask(task.getTestList());
    }

    public void saveFunctionArgument(FunctionArgument functionArgument){
        functionArgumentRepository.save(functionArgument);
    }
    public void saveFunctionArgument(List<FunctionArgument> functionArgumentList){
        functionArgumentRepository.saveAll(functionArgumentList);
    }

    public void saveTestTask(TestTask testTask){
        if(testTask != null){
            testTaskRepository.save(testTask);
            if(testTask.getTestArguments() != null && !testTask.getTestArguments().isEmpty()){
                saveTestArgument(testTask.getTestArguments());
            }
        }
    }
    public void saveTestTask(List<TestTask> testTaskList){
        if(testTaskList != null && !testTaskList.isEmpty()){
            testTaskRepository.saveAll(testTaskList);
            for (TestTask testTask : testTaskList) {
                saveTestArgument(testTask.getTestArguments());  //Zapisywanie wszystkich argumentów w TestTask
            }
        }
    }

    public void saveTestArgument(TestArgument testArgument){
        if(testArgument != null){
            testArgumentRepository.save(testArgument);
        }
    }
    public void saveTestArgument(List<TestArgument> testArgumentList){
        if(testArgumentList != null && !testArgumentList.isEmpty()){
            testArgumentRepository.saveAll(testArgumentList);
        }
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

        return null;
    }

    public List<FunctionArgument> mapFunctionArgumentDTOListToObject(List<FunctionArgumentDTO> functionArgumentDTOList){
        return functionArgumentDTOList.stream()
                .map(this::mapFunctionArgumentDTOtoObject)
                .collect(Collectors.toList());
    }


    //TestTask
    public TestTaskDTO mapTestTaskToDTO(TestTask testTask){
        return TestTaskDTO.builder()
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
                .type(testArgument.getType().toString())
                .testArgument(testArgument.getArgument())
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