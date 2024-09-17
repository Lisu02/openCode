package com.example.openCode.CompilationModule.Controller;

import com.example.openCode.CompilationModule.DTO.TaskDTO;
import com.example.openCode.CompilationModule.Model.Task;
import com.example.openCode.CompilationModule.Repository.TaskRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
public class TaskController {

    TaskRepository taskRepository;

    @GetMapping("/v1/task")
    public List<TaskDTO> getAllTasks(){
        //TODO: getAll tasks using DTO
        return null;
    }
}
