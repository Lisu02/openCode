package com.example.openCode.CompilationModule.Controller;

import com.example.openCode.CompilationModule.Model.UserCode;
import com.example.openCode.CompilationModule.Service.CompilationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@CrossOrigin(origins = "*")
public class CompileController {


    private CompilationService compilationService;
    private final AtomicLong codeID = new AtomicLong(0);

    @Autowired
    public CompileController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }


    @PostMapping(value = "/v2/compile")
    public String v2PostCompile(@RequestBody String code){
        System.out.println(code);
        System.out.println(code.codePointAt(0));
        return code;
    }

    @PostMapping(value = "/compile")
    public String postCompile(@RequestBody String code) {

        UserCode userCode = new UserCode(incrementCodeID(), "C", code);
        System.out.println(code);
        return compilationService.compileUserCode(userCode);
    }

    synchronized long incrementCodeID() {
        return codeID.incrementAndGet();
    }
}