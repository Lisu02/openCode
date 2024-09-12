package com.example.openCode.CompilationModule.Controller;

import com.example.openCode.CompilationModule.Model.UserCode;
import com.example.openCode.CompilationModule.Service.CompilationService;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@RestController
@CrossOrigin(origins = "*")
public class CompileController {


    private CompilationService compilationService;
    private final AtomicLong codeID = new AtomicLong(0);

    @Autowired
    public CompileController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }


    //TODO: FIX configuration in spring security 403 error
    @PostMapping(value = "/compile",consumes = MediaType.APPLICATION_JSON_VALUE)
    public String postCompile(@RequestBody UserCode code) {

        UserCode userCode = new UserCode(incrementCodeID(),"C",code.getUserCode());
        System.out.println(code);
        return compilationService.compileUserCode(userCode);
    }

    synchronized long incrementCodeID(){
        return codeID.incrementAndGet();
    }
}