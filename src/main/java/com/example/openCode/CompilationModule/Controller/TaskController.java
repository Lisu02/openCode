package com.example.openCode.CompilationModule.Controller;

import com.example.openCode.CompilationModule.DTO.*;
import com.example.openCode.CompilationModule.Model.Task.ReturnType;
import com.example.openCode.CompilationModule.Model.Task.FunctionArgument;
import com.example.openCode.CompilationModule.Model.Task.Task;
import com.example.openCode.CompilationModule.Model.Task.TestTask.TestArgument;
import com.example.openCode.CompilationModule.Model.Task.TestTask.TestTask;
import com.example.openCode.CompilationModule.Model.Users.UserPrincipal;
import com.example.openCode.CompilationModule.Model.Users.Users;
import com.example.openCode.CompilationModule.Service.DockerHandler.GCC.DockerTaskGCC;
import com.example.openCode.CompilationModule.Service.DockerHandler.PYTHON3.DockerTaskPython3;
import com.example.openCode.CompilationModule.Service.Task.TaskService;
import com.example.openCode.CompilationModule.Service.UserSecurity.MyUserDetailsService;
import com.example.openCode.CompilationModule.Service.UserSecurity.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@RestController()
public class TaskController {

    TaskService taskService;
    MyUserDetailsService myUserDetailsService;
    DockerTaskGCC dockerTaskGCC;
    DockerTaskPython3 dockerTaskPython3;
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);


    @Autowired
    public TaskController(TaskService taskService, MyUserDetailsService myUserDetailsService, DockerTaskGCC dockerTaskGCC, DockerTaskPython3 dockerTaskPython3) {
        this.taskService = taskService;
        this.myUserDetailsService = myUserDetailsService;
        this.dockerTaskGCC = dockerTaskGCC;
        this.dockerTaskPython3 = dockerTaskPython3;
    }

    @PostMapping("/v1/addTask")
    public void addTask(@RequestBody TaskDTO taskDTO) {

        // Pobranie nazwy użytkownika z kontekstu bezpieczeństwa (tokenu)
        String username = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        // Pobranie użytkownika z MyUserDetailsService na podstawie nazwy użytkownika
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(username); // Wykorzystanie loadUserByUsername

        // Uzyskanie obiektu Users z UserDetails
        Users user = ((UserPrincipal) userDetails).getUser();  // Rzutowanie na UserPrincipal i uzyskanie obiektu Users


        List<FunctionArgument> funArgList = new LinkedList<>();
        List<TestTask> testTaskList = new LinkedList<>();

        for (FunctionArgumentDTO funArgDTO : taskDTO.getArgumentList()) {
            FunctionArgument functionArgument = FunctionArgument.builder()
                    .name(funArgDTO.getName())
                    .type(ReturnType.valueOf(funArgDTO.getType()))
                    .task(null)
                    .build();
            funArgList.add(functionArgument);
        }

        for(TestTaskDTO testTaskDTO : taskDTO.getTestList()) {
            List<TestArgument> testArgList = new LinkedList<>();
            for(TestInputArgumentDTO testInputArgumentDTO : testTaskDTO.getTestInputArgumentDTOList()){
                TestArgument testArgument = TestArgument.builder()
                        .argument(testInputArgumentDTO.getTestArgument())
                        .type(ReturnType.valueOf(testInputArgumentDTO.getType()))
                        .size(testInputArgumentDTO.getSize())
                        .testTask(null)
                        .build();
                testArgList.add(testArgument);
            }

            TestTask testTask = TestTask.builder()
                    .expectedValue(testTaskDTO.getExpectedValue())
                    .size(testTaskDTO.getSize())
                    .task(null)
                    .testArguments(testArgList)
                    .build();
            testTaskList.add(testTask);
            testArgList.forEach(it -> it.setTestTask(testTask));
        }

        //TODO: Sprawdzic czy do listy testow i ich argumentow poprawnie jest wszystko podpiete z id

        Task task = Task.builder()
                .returnType(ReturnType.valueOf(taskDTO.getReturnType()))
                .functionName(taskDTO.getFunctionName())
                .argumentList(funArgList)
                .testList(testTaskList)
                .user(user)
                .build();

        funArgList.forEach(it -> it.setTask(task));
        testTaskList.forEach(it -> it.setTask(task));


        System.out.println(funArgList);
        System.out.println(testTaskList);
        System.out.println(task);

        taskService.saveTask(task);
        Task tmp = taskService.getTaskById(task.getId());
        if(tmp != null){
            generateTask(tmp.getId());
        }
    }


    @GetMapping("/v1/addTasksTest")
    public void addTaskForTestingPurposes(){
        //TODO: POPRAWIĆ ZAPISYWANIE ZŁOŻONYCH TESTÓW PONIEWAŻ FUNKCJA GENERUJACA TESTY ROBI BŁEDY NA BŁEDNYCH DANYCH
        //TODO: POPRAWIĆ BRAKI W BAZIE DANYCH PO DODANIU ZADANIA TESTOWEGO
        LinkedList<FunctionArgument> functionArguments = new LinkedList<>();
        functionArguments.add(new FunctionArgument(1,ReturnType.INT,"liczba",null));
        functionArguments.add(new FunctionArgument(2,ReturnType.INT,"mnoznik",null));

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

    @GetMapping("/v1/addTasksTest222")
    public void addTaskForTestingPurposes2() {
        System.out.println("addTaskForTestingPurposes2?");
        LinkedList<FunctionArgument> functionArguments = new LinkedList<>();
        functionArguments.add(new FunctionArgument(0, ReturnType.BOOLEAN, "czyDodawacLitere", null));
        functionArguments.add(new FunctionArgument(0, ReturnType.CHAR, "litera", null));
        functionArguments.add(new FunctionArgument(0, ReturnType.DOUBLE, "numer1", null));
        functionArguments.add(new FunctionArgument(0, ReturnType.DOUBLE, "numer2", null));

        LinkedList<TestArgument> testArguments = new LinkedList<>();
        testArguments.add(new TestArgument(0, ReturnType.BOOLEAN, "true", null));
        testArguments.add(new TestArgument(0, ReturnType.CHAR, "A", null));
        testArguments.add(new TestArgument(0, ReturnType.DOUBLE, "12.5", null));
        testArguments.add(new TestArgument(0, ReturnType.DOUBLE, "7.5", null));

        LinkedList<TestArgument> testArguments2 = new LinkedList<>();
        testArguments2.add(new TestArgument(0, ReturnType.BOOLEAN, "false", null));
        testArguments2.add(new TestArgument(0, ReturnType.CHAR, "X", null));
        testArguments2.add(new TestArgument(0, ReturnType.DOUBLE, "80.0", null));
        testArguments2.add(new TestArgument(0, ReturnType.DOUBLE, "20.0", null));

        List<TestTask> testTaskList = new LinkedList<>();
        testTaskList.add(new TestTask(0, null, testArguments, "A20.0"));
        testTaskList.add(new TestTask(0, null, testArguments2, "100.0"));

        System.out.println("\nBefore foreach");

        testArguments.forEach(it -> it.setTestTask(testTaskList.get(0)));
        testArguments2.forEach(it -> it.setTestTask(testTaskList.get(1)));

        Task task = Task.builder()
                .returnType(ReturnType.STRING)
                .functionName("LiteryLiczby")
                .argumentList(functionArguments)
                .testList(testTaskList)
                .build();

        // Set the Task reference in each FunctionArgument
        functionArguments.forEach(arg -> arg.setTask(task));

        testTaskList.forEach(it -> it.setTask(task));


        System.out.println(task.getId() + task.getFunctionName() + task.getReturnType() + task.getTestList() + task.getArgumentList());

        taskService.saveTask(task);
        Task tmp = taskService.getTaskById(task.getId());
        if(tmp != null){
            generateTask(tmp.getId());
        }
    }

    @GetMapping("/v1/addTasksTest333")
    public void addTasksForTestingPurposes3(){
        LinkedList<FunctionArgument> functionArguments = new LinkedList<>();
        functionArguments.add(new FunctionArgument(0, ReturnType.INTVECTOR, "array1", null));
        functionArguments.add(new FunctionArgument(0, ReturnType.INTVECTOR, "array2", null));


        LinkedList<TestArgument> testArguments = new LinkedList<>();
        testArguments.add(new TestArgument(0, ReturnType.INTVECTOR, "1,5,10,15",4, null));
        testArguments.add(new TestArgument(0, ReturnType.INTVECTOR, "9,0,10,35",4, null));

        List<TestTask> testTaskList = new LinkedList<>();
        testTaskList.add(new TestTask(0, null, testArguments, "10,5,20,50",4));


        testArguments.forEach(it -> it.setTestTask(testTaskList.get(0)));

        Task task = Task.builder()
                .returnType(ReturnType.INTVECTOR)
                .functionName("ArraySum")
                .argumentList(functionArguments)
                .testList(testTaskList)
                .build();

        // Set the Task reference in each FunctionArgument
        functionArguments.forEach(arg -> arg.setTask(task));

        testTaskList.forEach(it -> it.setTask(task));

        taskService.saveTask(task);
        Task tmp = taskService.getTaskById(task.getId());
        if(tmp != null){
            generateTask(tmp.getId());
        }
    }

    @GetMapping("/v1/addTasksTest444")
    public String addTasksForTestingPurposes4() {
        System.out.println("ADD TASKS FOR TESTING 4");

        // Argumenty funkcji
        LinkedList<FunctionArgument> functionArguments = new LinkedList<>();
        functionArguments.add(new FunctionArgument(0, ReturnType.INT, "x", null));

        // Test Argumenty (dla każdego TestTask osobne listy)
        LinkedList<TestArgument> testArguments = new LinkedList<>();
        testArguments.add(new TestArgument(0, ReturnType.INT, "153", null));

        LinkedList<TestArgument> testArguments2 = new LinkedList<>();
        testArguments2.add(new TestArgument(0, ReturnType.INT, "121", null));

        LinkedList<TestArgument> testArguments3 = new LinkedList<>();
        testArguments3.add(new TestArgument(0, ReturnType.INT, "333", null));

        LinkedList<TestArgument> testArguments4 = new LinkedList<>();
        testArguments4.add(new TestArgument(0, ReturnType.INT, "1942", null));

        LinkedList<TestArgument> testArguments5 = new LinkedList<>();
        testArguments5.add(new TestArgument(0, ReturnType.INT, "129821", null));

        // Lista TestTask
        List<TestTask> testTaskList = new LinkedList<>();
        testTaskList.add(new TestTask(0, null, testArguments, "false"));
        testTaskList.add(new TestTask(0, null, testArguments2, "true"));
        testTaskList.add(new TestTask(0, null, testArguments3, "true"));
        testTaskList.add(new TestTask(0, null, testArguments4, "false"));
        testTaskList.add(new TestTask(0, null, testArguments5, "false"));

        // Przypisywanie relacji testArgument -> testTask
        testArguments.forEach(it -> it.setTestTask(testTaskList.get(0)));
        testArguments2.forEach(it -> it.setTestTask(testTaskList.get(1)));
        testArguments3.forEach(it -> it.setTestTask(testTaskList.get(2)));
        testArguments4.forEach(it -> it.setTestTask(testTaskList.get(3)));
        testArguments5.forEach(it -> it.setTestTask(testTaskList.get(4)));

        // Tworzenie Task
        Task task = Task.builder()
                .returnType(ReturnType.BOOLEAN)
                .functionName("PalindromNumber")
                .argumentList(functionArguments)
                .testList(testTaskList)
                .build();

        // Przypisywanie relacji argument -> task i testTask -> task
        functionArguments.forEach(arg -> arg.setTask(task));
        testTaskList.forEach(it -> it.setTask(task));

        // Zapis zadania w bazie danych
        taskService.saveTask(task);
        Task tmp = taskService.getTaskById(task.getId());
        System.out.println(tmp);

        if (tmp != null) {
            generateTask(tmp.getId());
            return "OK";
        } else {
            log.warn("TMP WHEN ADDING IS NULL");
        }

        return "NOT OK";
    }



    @GetMapping("/v1/task")
    public List<TaskDTO> getAllTasks(){
        return taskService.getAllTasksDTO();
    }

    @GetMapping("/v1/taskId")
    public List<TaskSmallDTO> getAllTasksId(){
        Iterator<Task> taskIterator =  taskService.getAllTasks().iterator();
        List<TaskSmallDTO> taskList = new LinkedList<>();
        String username = "username-not-assigned";
        while(taskIterator.hasNext()){
            Task task = taskIterator.next();

            if(task.getUser() != null){
                username = task.getUser().getUsername();
            }
            TaskSmallDTO taskSmallDTO = TaskSmallDTO.builder()
                    .taskId(task.getId())
                    .returnType(String.valueOf(task.getReturnType()))
                    .functionName(task.getFunctionName())
                    .creatorUsername(username)
                    .build();
            taskList.add(taskSmallDTO);
        }
        return taskList;
    }

    @GetMapping("/v1/task/{id}")
    public TaskDTO getTaskById(@PathVariable("id") long id){
        return taskService.getTaskDTObyId(id);
    }

    @GetMapping("/v1/generateTask/{id}")
    public void generateTask(@PathVariable("id")long id){
        Task task = taskService.getTaskById(id);
        if(task != null){
            dockerTaskGCC.createTaskInContainer(task);
            dockerTaskPython3.createTaskInContainer(task);
        }else{
            log.warn("TASK NOT FOUND IN DATABASE");
            //System.out.println("BRAK TASKA");
        }
    }


    @PostMapping("/v1/task")
    public void putTask(@RequestBody TaskDTO taskDTO){
        taskService.saveTaskDTO(taskDTO);
    }

    @GetMapping("/v1/task/description/{id}")
    public void getTaskDescription(@PathVariable("id") long id){
        Task task = taskService.getTaskById(id);
        task.getTaskDescription();
    }


    @DeleteMapping("/v1/task/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable long id) {
        // Pobranie nazwy użytkownika z kontekstu bezpieczeństwa (tokenu)
        String username = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        // Pobranie użytkownika z MyUserDetailsService na podstawie nazwy użytkownika
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(username); // Wykorzystanie loadUserByUsername

        // Uzyskanie obiektu Users z UserDetails
        Users user = ((UserPrincipal) userDetails).getUser();  // Rzutowanie na UserPrincipal i uzyskanie obiektu Users;

        Task task = taskService.getTaskById(id);
        if (task == null) {
            return new ResponseEntity<>("Task not found", HttpStatus.NOT_FOUND);
        }

        if (task.getUser().getId() != user.getId()) {
            return new ResponseEntity<>("Unauthorized to delete this task", HttpStatus.FORBIDDEN);
        }

        taskService.removeTask(task);
        return new ResponseEntity<>("Task deleted successfully", HttpStatus.OK);
    }


}
