package com.example.openCode.CompilationModule.Service;

import com.example.openCode.CompilationModule.Model.PlaygroundCode;
//import com.example.openCode.CompilationModule.Prototype.DockerPlaygroundGCC;
import com.example.openCode.CompilationModule.Service.DockerHandler.GCC.DockerPlaygroundGCC;
import org.springframework.stereotype.Service;

@Service
public class CompilationService {

    private DockerPlaygroundGCC dockerPlaygroundGCC;

    public CompilationService(DockerPlaygroundGCC dockerPlaygroundGCC) {
        this.dockerPlaygroundGCC = dockerPlaygroundGCC;
    }

    public String compileUserCode(PlaygroundCode playgroundCode) {

        String compilationOutput = switch (playgroundCode.getProgrammingLanguage()) {
            case "PYTHON3" -> compileUserCodePython3(playgroundCode);
            case "C" -> compileUserCodeC(playgroundCode);
            default -> "UNKNOWN LANGUAGE SELECTION";
        };

        return compilationOutput;
    }

    private String compileUserCodePython3(PlaygroundCode playgroundCode) {
        //TODO: Java w przyszlosci
        return "NO PYTHON3 IMPLEMENTATION YET";
    }

    private String compileUserCodeC(PlaygroundCode playgroundCode) {
        //TODO: Implementacja prototypu kompilatora GCC
        String compileOutput = "";
        compileOutput += dockerPlaygroundGCC.compile(playgroundCode);
        if (compileOutput.isEmpty()) {
            compileOutput += dockerPlaygroundGCC.runCode(playgroundCode);
        } else {
            compileOutput += "COMPILATION ERROR:";
        }
        return compileOutput;
    }


}
