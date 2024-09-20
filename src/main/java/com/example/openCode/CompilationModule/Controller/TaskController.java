package com.example.openCode.CompilationModule.Controller;

import com.example.openCode.CompilationModule.DTO.TaskDTO;
import com.example.openCode.CompilationModule.Model.ReturnType;
import com.example.openCode.CompilationModule.Model.Task.FunctionArgument;
import com.example.openCode.CompilationModule.Model.Task.Task;
import com.example.openCode.CompilationModule.Model.TestTask.TestArgument;
import com.example.openCode.CompilationModule.Model.TestTask.TestTask;
import com.example.openCode.CompilationModule.Service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController()
public class TaskController {

    TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }


    @GetMapping("/v1/addTasksTest")
    public void addTaskForTestingPurposes(){

        LinkedList<FunctionArgument> functionArguments = new LinkedList<>();
        functionArguments.add(new FunctionArgument(0,ReturnType.INT,"liczba",null));
        functionArguments.add(new FunctionArgument(1,ReturnType.INT,"mnoznik",null));

        LinkedList<TestArgument> testArguments = new LinkedList<>();
        testArguments.add(new TestArgument(1,ReturnType.INT,"4",null));
        testArguments.add(new TestArgument(2,ReturnType.INT,"10",null));

        TestTask testTask = new TestTask(0,null,testArguments,"40");

        LinkedList<TestTask> testTaskLinkedList = new LinkedList<>();
        testTaskLinkedList.add(testTask);

        Task task = new Task(0,ReturnType.INT,"power", functionArguments, testTaskLinkedList);

        functionArguments.get(0).setTask(task);
        functionArguments.get(1).setTask(task);
        testTask.setTask(task);
        testArguments.get(0).setTestTask(testTask);
        testArguments.get(1).setTestTask(testTask);

        taskService.saveTask(task);
    }

    @GetMapping("/v1/task")
    public List<TaskDTO> getAllTasks(){
        return taskService.getAllTasksDTO();
    }

    @GetMapping("/v1/task/{id}")
    public TaskDTO getTaskById(@PathVariable("id") long id){
        return taskService.getTaskDTObyId(id);
    }

    @PostMapping("/v1/task")
    public void putTask(@RequestBody TaskDTO taskDTO){
        taskService.saveTaskDTO(taskDTO);
    }
}
