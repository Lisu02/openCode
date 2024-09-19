package com.example.openCode.CompilationModule.Service.Exception;

public class TaskNotFoundException extends RuntimeException{

    public TaskNotFoundException(String msg){
        super(msg);
    }
}
