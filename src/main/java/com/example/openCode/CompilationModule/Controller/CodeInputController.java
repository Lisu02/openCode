package com.example.openCode.CompilationModule.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CodeInputController {


    @GetMapping("/input")
    public String input() {
        return "input.html";
    }

}
