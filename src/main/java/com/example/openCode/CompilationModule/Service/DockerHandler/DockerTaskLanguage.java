package com.example.openCode.CompilationModule.Service.DockerHandler;

import com.example.openCode.CompilationModule.Model.Task.Task;

public interface DockerTaskLanguage {

    public Boolean isTaskCreatedInDockerContainer(Task task);
}
