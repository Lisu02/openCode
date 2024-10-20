package com.example.openCode.CompilationModule.Controller;

import com.example.openCode.CompilationModule.DTO.TaskDTO;
import com.example.openCode.CompilationModule.Model.Task.ReturnType;
import com.example.openCode.CompilationModule.Model.Task.FunctionArgument;
import com.example.openCode.CompilationModule.Model.Task.Task;
import com.example.openCode.CompilationModule.Model.Task.TestTask.TestArgument;
import com.example.openCode.CompilationModule.Model.Task.TestTask.TestTask;
import com.example.openCode.CompilationModule.Service.DockerHandler.GCC.DockerTaskGCC;
import com.example.openCode.CompilationModule.Service.Task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController()
public class TaskController {

    TaskService taskService;
    DockerTaskGCC dockerTaskGCC;

    @Autowired
    public TaskController(TaskService taskService, DockerTaskGCC dockerTaskGCC){
        this.taskService = taskService;
        this.dockerTaskGCC = dockerTaskGCC;
    }


    @GetMapping("/v1/addTasksTest")
    public void addTaskForTestingPurposes(){
        //TODO: POPRAWIĆ ZAPISYWANIE ZŁOŻONYCH TESTÓW PONIEWAŻ FUNKCJA GENERUJACA TESTY ROBI BŁEDY NA BŁEDNYCH DANYCH
        //TODO: POPRAWIĆ BRAKI W BAZIE DANYCH PO DODANIU ZADANIA TESTOWEGO
        LinkedList<FunctionArgument> functionArguments = new LinkedList<>();
        functionArguments.add(new FunctionArgument(0,ReturnType.INT,"liczba",null));
        functionArguments.add(new FunctionArgument(1,ReturnType.INT,"mnoznik",null));

        LinkedList<TestArgument> testArguments = new LinkedList<>();
        testArguments.add(new TestArgument(1,ReturnType.INT,"4",null));
        testArguments.add(new TestArgument(2,ReturnType.INT,"10",null));

        LinkedList<TestArgument> testArguments2 = new LinkedList<>();
        testArguments2.add(new TestArgument(1,ReturnType.INT,"10",null));
        testArguments2.add(new TestArgument(2,ReturnType.INT,"5",null));


        TestTask testTask = new TestTask(0,null,testArguments,"40");

        TestTask testTask2 = new TestTask(0,null,testArguments2,"50");

        LinkedList<TestTask> testTaskLinkedList = new LinkedList<>();
        testTaskLinkedList.add(testTask);
        testTaskLinkedList.add(testTask2);

        Task task = new Task(0,ReturnType.INT,"mnozenie", functionArguments, testTaskLinkedList);

        functionArguments.get(0).setTask(task);
        functionArguments.get(1).setTask(task);
        testTask.setTask(task);
        testTask2.setTask(task);

        testArguments.get(0).setTestTask(testTask);
        testArguments.get(1).setTestTask(testTask);

        testArguments2.get(0).setTestTask(testTask2);
        testArguments2.get(1).setTestTask(testTask2);

        taskService.saveTask(task);
        Task tmp = taskService.getTaskById(task.getId());
        if(tmp != null){
            generateTask(tmp.getId());
        }
    }

    @GetMapping("/v1/task")
    public List<TaskDTO> getAllTasks(){
        return taskService.getAllTasksDTO();
    }

    @GetMapping("/v1/task/{id}")
    public TaskDTO getTaskById(@PathVariable("id") long id){
        return taskService.getTaskDTObyId(id);
    }

    @GetMapping("/v1/addTask/{id}")
    public void generateTask(@PathVariable("id")long id){
        Task task = taskService.getTaskById(id);
        if(task != null){
            dockerTaskGCC.createTaskInContainer(task);
        }else{
            System.out.println("BRAK TASKA");
        }
    }


    @PostMapping("v1/task/resolve/{id}")
    public String resolveTask(@PathVariable("id") long id,String code){
        Task task = taskService.getTaskById(id);
        if(task != null){
            //return taskService.solveTask(code);
            return null;
        }else{
            return "TASK NOT FOUND";
        }
    }

    @PostMapping("/v1/task")
    public void putTask(@RequestBody TaskDTO taskDTO){
        taskService.saveTaskDTO(taskDTO);
    }
}
