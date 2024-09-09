package com.example.openCode.CompilationModule.Controller;

import com.example.openCode.CompilationModule.Model.UserCode;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@CrossOrigin(origins = "*")
public class CompileController {


    @PostMapping("/compile")
    public String post(@RequestBody String code) {

        UserCode userCode = new UserCode(0L,"Java",code);
        System.out.println(userCode);
        Logger logger = Logger.getAnonymousLogger();
        logger.info(userCode.getUserCode());
        //TODO: Return JSON with result code and compilation/test status
        return userCode.toString();
    }
}
