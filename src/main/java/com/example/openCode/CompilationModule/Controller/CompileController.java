package com.example.openCode.CompilationModule.Controller;

import com.example.openCode.CompilationModule.Model.UserCode;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@CrossOrigin(origins = "*")
public class CompileController {

    String code = "test";



    @GetMapping("/compile")
    public String getCompile(){
        System.out.println("getCompile");
        return code;
    }

    @PostMapping("/compile")
    public String postCompile(@RequestBody String code) {
        this.code = code;
        UserCode userCode = new UserCode(0L,"Java",code);
        System.out.println(userCode);
        Logger logger = Logger.getAnonymousLogger();
        logger.info(userCode.getUserCode());
        //TODO: Return JSON with result code and compilation/test status
        return userCode.toString();
    }
}
