package com.example.openCode.CompilationModule.Service;

import com.example.openCode.CompilationModule.Model.UserCode;
//import com.example.openCode.CompilationModule.Prototype.DockerCompilatorGCC;
import com.example.openCode.CompilationModule.Prototype.DockerCompilatorGCC;
import org.springframework.stereotype.Service;

@Service
public class CompilationService {

    private DockerCompilatorGCC dockerCompilatorGCC;

    public CompilationService(DockerCompilatorGCC dockerCompilatorGCC) {
        this.dockerCompilatorGCC = dockerCompilatorGCC;
    }

    public String compileUserCode(UserCode userCode){

        String compilationOutput = switch (userCode.getProgrammingLanguage()) {
            case "JAVA" -> compileUserCodeJava(userCode);
            case "C" -> compileUserCodeC(userCode);
            default -> "UNKNOWN LANGUAGE SELECTION";
        };

        return compilationOutput;
    }

    private String compileUserCodeJava(UserCode userCode){
        //TODO: Java w przyszlosci
        return "NO JAVA IMPLEMENTATION YET";
    }
    private String compileUserCodeC(UserCode userCode){
        //TODO: Implementacja prototypu kompilatora GCC
        String compileOutput = "";
        compileOutput += dockerCompilatorGCC.compile(userCode.getUserCode());
        compileOutput += dockerCompilatorGCC.runCode();
        return compileOutput;
    }


}
