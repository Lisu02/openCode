package com.example.openCode.CompilationModule.Controller;

import com.example.openCode.CompilationModule.DTO.UserSolutionDTO;
import com.example.openCode.CompilationModule.Model.UserSolution;
import com.example.openCode.CompilationModule.Service.SolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@CrossOrigin(origins = "*")
public class SolutionController {

    private SolutionService solutionService;
    private final AtomicLong codeID = new AtomicLong(0);


    @Autowired
    public SolutionController(SolutionService solutionService) {
        this.solutionService = solutionService;
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

        UserSolution userSolution = UserSolution.builder()
                .solvingTaskId(userSolutionDTO.getSolvingTaskId())
                .programmingLanguage(userSolutionDTO.getProgrammingLanguage())
                .solutionCode(userSolutionDTO.getSolutionCode())
                .build();

        return solutionService.solve(userSolution);
    }

    private synchronized long incrementCodeID() {
        return codeID.incrementAndGet();
    }


}
