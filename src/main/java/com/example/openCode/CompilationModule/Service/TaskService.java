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
    TaskMapper taskMapper;


    @Autowired
    public TaskService(TaskRepository taskRepository,
                       FunctionArgumentRepository functionArgumentRepository,
                       TestTaskRepository testTaskRepository,
                       TestArgumentRepository testArgumentRepository,
                       TaskMapper taskMapper
    ){
        this.taskRepository = taskRepository;
        this.functionArgumentRepository = functionArgumentRepository;
        this.testTaskRepository = testTaskRepository;
        this.testArgumentRepository = testArgumentRepository;
        this.taskMapper = taskMapper;
    }

    //-------------GETTING TASKS-------------

    public List<TaskDTO> getTaskDTOList(){
        List<Task> taskList = taskRepository.findAll();
        return taskMapper.mapTaskListToTaskDTOList(taskList);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<TaskDTO> getAllTasksDTO(){
        List<Task> taskList = taskRepository.findAll();
        return taskMapper.mapTaskListToTaskDTOList(taskList);
    }

    public TaskDTO getTaskDTObyId(long id){
        Optional<Task> taskDTO = taskRepository.findById(id);
        if(taskDTO.isEmpty()) return null;
        return taskMapper.mapTaskToDTO(taskDTO.get());
    }

    //-----------SAVING TASK---------------

    public void saveTaskDTO(TaskDTO taskDTO){
        saveTask(taskMapper.mapTaskDTOtoTask(taskDTO));
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
}