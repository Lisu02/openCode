package com.example.openCode.CompilationModule.Service.Task;

import com.example.openCode.CompilationModule.DTO.TaskDTO;
import com.example.openCode.CompilationModule.Model.Task.FunctionArgument;
import com.example.openCode.CompilationModule.Model.Task.ReturnType;
import com.example.openCode.CompilationModule.Model.Task.Task;
import com.example.openCode.CompilationModule.Model.Task.TestTask.TestArgument;
import com.example.openCode.CompilationModule.Model.Task.TestTask.TestTask;
import com.example.openCode.CompilationModule.Repository.FunctionArgumentRepository;
import com.example.openCode.CompilationModule.Repository.TaskRepository;
import com.example.openCode.CompilationModule.Repository.TestArgumentRepository;
import com.example.openCode.CompilationModule.Repository.TestTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    TaskRepository taskRepository;
    FunctionArgumentRepository functionArgumentRepository;
    TestTaskRepository testTaskRepository;
    TestArgumentRepository testArgumentRepository;
    TaskMapper taskMapper;

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);


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

    //---------------DOCKER ----------------------

    public static boolean isTaskReadyForCreation(Task task){
        if(task.getArgumentList() == null || task.getTestList() == null){
            log.warn("Task: {}is not ready for creation inside a container", task.getId());
            return false;
        }
        Iterator<TestTask> iterator = task.getTestList().iterator();
        TestTask testTaskTmp;
        while(iterator.hasNext()){
            testTaskTmp = iterator.next();
            if(testTaskTmp.getTestArguments() == null || testTaskTmp.getExpectedValue().isBlank()){
                log.warn("Task: {}is not ready for creation inside a container", task.getId());
                return false;
            }
        }
        return true;
    }

    public static boolean isTaskArrayType(Task task){
        return isTypeAnArrayType(task.getReturnType());
    }

    public static boolean isTypeAnArrayType(ReturnType returnType){
        return switch (returnType){
            case INTVECTOR -> true;
            case CHARVECTOR -> true;
            case STRING -> true;
            default -> false;
        };
    }

    public static int getAmountOfArrayType(Task task){
        List<FunctionArgument> argumentList = task.getArgumentList();
        int arrayTypeAmount = 0;
        for(FunctionArgument argument : argumentList){
            if(isTypeAnArrayType(argument.getType())){
                arrayTypeAmount++;
            }
        }
        return arrayTypeAmount;
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

    public Task getTaskById(long id){
        Optional<Task> task = taskRepository.findById(id);
        return task.orElse(null);
    }

    public TestTask getTestTaskById(Long testTaskId) {
        Optional<TestTask> testTask =  testTaskRepository.findById(testTaskId);
        return testTask.orElse(null);
    }


    //-----------SAVING TASK---------------

    public void saveTaskDTO(TaskDTO taskDTO){
        saveTask(taskMapper.mapTaskDTOtoTask(taskDTO));
    }

    public void saveTask(Task task){
        Task taskSave = taskRepository.save(task);
        if(task != taskSave) log.warn("task != taskSave");
        saveFunctionArgument(task.getArgumentList());
        saveTestTask(task.getTestList());
    }

    public void saveFunctionArgument(FunctionArgument functionArgument){
        functionArgumentRepository.save(functionArgument);
    }
    public void saveFunctionArgument(List<FunctionArgument> functionArgumentList){
        List<FunctionArgument> functionArgumentListSave = functionArgumentRepository.saveAll(functionArgumentList);
        if(!functionArgumentList.equals(functionArgumentListSave)){
            log.warn("functionArgumentList != functionArgumentListSave");
        }
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
            List<TestTask> testTasksSave = testTaskRepository.saveAll(testTaskList);
            if(!testTaskList.equals(testTasksSave)){log.warn("testTaskList != testTaskListSave");}
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
            List<TestArgument> testArgumentsSave = testArgumentRepository.saveAll(testArgumentList);
            if(testArgumentList.equals(testArgumentsSave)){log.warn("testArgumentList != testArgumentListSave");}
        }
    }

    public boolean taskExist(Long solvingTaskId) {
        return getTaskById(solvingTaskId) != null;
    }

    public void addTaskToDocker(Task task) {
        //Adding a task to a docker with test.c generator
    }

}