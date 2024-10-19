package com.example.openCode.CompilationModule.Controller;

import com.example.openCode.CompilationModule.Model.UserSolution;
import com.example.openCode.CompilationModule.Service.SolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class SolutionController {

    private SolutionService solutionService;

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

}
