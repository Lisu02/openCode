package com.example.openCode.CompilationModule.Controller;

import com.example.openCode.CompilationModule.Model.UserCode;
import com.example.openCode.CompilationModule.Service.CompilationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    @PostMapping("/compile")
    public String postCompile(@RequestBody String code) {

        UserCode userCode = new UserCode(incrementCodeID(),"C",code);
        //System.out.println(code);
        return compilationService.compileUserCode(userCode);
    }

    synchronized long incrementCodeID(){
        return codeID.incrementAndGet();
    }
}
