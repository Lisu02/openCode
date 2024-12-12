package com.example.openCode.CompilationModule.Controller;

import com.example.openCode.CompilationModule.DTO.TaskSolutionDataDTO;
import com.example.openCode.CompilationModule.DTO.UserSolutionDTO;
import com.example.openCode.CompilationModule.Model.UserSolution;
import com.example.openCode.CompilationModule.Model.Users.UserPrincipal;
import com.example.openCode.CompilationModule.Model.Users.Users;
import com.example.openCode.CompilationModule.Service.SolutionService;
import com.example.openCode.CompilationModule.Service.Task.TaskService;
import com.example.openCode.CompilationModule.Service.UserSecurity.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class SolutionController {

    private final TaskService taskService;
    private SolutionService solutionService;
    private MyUserDetailsService myUserDetailsService;
    private final AtomicLong codeID = new AtomicLong(0);


    @Autowired
    public SolutionController(SolutionService solutionService, TaskService taskService, MyUserDetailsService myUserDetailsService) {
        this.solutionService = solutionService;
        this.taskService = taskService;
        this.myUserDetailsService = myUserDetailsService;
    }


    @PostMapping("/solve/{id}")
    public String solveTask(@PathVariable long id, @RequestBody String solution) {

        UserSolution userSolution = UserSolution.builder()
                .solvingTaskId(id)
                .solutionCode(solution)
                .build();

        return solutionService.solve(userSolution);
    }

    @PostMapping("/solve")
    public String solveTask(@RequestBody UserSolutionDTO userSolutionDTO) {

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

        UserSolution userSolution = UserSolution.builder()
                .solvingTaskId(userSolutionDTO.getSolvingTaskId())
                .programmingLanguage(userSolutionDTO.getProgrammingLanguage())
                .solutionCode(userSolutionDTO.getSolutionCode())
                .user(user)
                .build();

        return solutionService.solve(userSolution);
    }

    @GetMapping("/solve/{id}")
    public ResponseEntity<?> getTaskInfo(@PathVariable long id) {
        Map<String, String> response = new HashMap<>();

        solutionService.getTaskInfo(id, response);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    private synchronized long incrementCodeID() {
        return codeID.incrementAndGet();
    }


}
