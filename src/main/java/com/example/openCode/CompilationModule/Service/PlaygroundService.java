package com.example.openCode.CompilationModule.Service;

import com.example.openCode.CompilationModule.Model.PlaygroundCode;
import com.example.openCode.CompilationModule.Service.DockerHandler.GCC.DockerPlaygroundGCC;
import org.springframework.stereotype.Service;

@Service
public class PlaygroundService {

    private DockerPlaygroundGCC dockerPlaygroundGCC;

    public PlaygroundService(DockerPlaygroundGCC dockerPlaygroundGCC) {
        this.dockerPlaygroundGCC = dockerPlaygroundGCC;
    }


    public String compilePlaygroundCode(PlaygroundCode playgroundCode) {
        String compilationOutput = switch (playgroundCode.getProgrammingLanguage()) {
            case "JAVA" -> compileUserCodeJava(playgroundCode);
            case "C" -> compileUserCodeC(playgroundCode);
            default -> "UNKNOWN LANGUAGE SELECTION";
        };

        return compilationOutput;
    }

    private String compileUserCodeJava(PlaygroundCode playgroundCode) {
        //TODO: Java w przyszlosci
        return "NO JAVA IMPLEMENTATION YET";
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
