package com.example.openCode.CompilationModule.Service;

import com.example.openCode.CompilationModule.Model.PlaygroundCode;
import com.example.openCode.CompilationModule.Service.DockerHandler.GCC.DockerPlaygroundGCC;
import com.example.openCode.CompilationModule.Service.DockerHandler.PYTHON3.DockerPlaygroundPython3;
import org.springframework.stereotype.Service;

@Service
public class PlaygroundService {

    private DockerPlaygroundGCC dockerPlaygroundGCC;
    private DockerPlaygroundPython3 dockerPlaygroundPython3;

    public PlaygroundService(DockerPlaygroundGCC dockerPlaygroundGCC
            , DockerPlaygroundPython3 dockerPlaygroundPython3) {
        this.dockerPlaygroundGCC = dockerPlaygroundGCC;
        this.dockerPlaygroundPython3 = dockerPlaygroundPython3;
    }


    public String compilePlaygroundCode(PlaygroundCode playgroundCode) {
        String compilationOutput = switch (playgroundCode.getProgrammingLanguage()) {
            case "PYTHON3" -> compileUserCodePython3(playgroundCode);
            case "C" -> compileUserCodeC(playgroundCode);
            default -> "UNKNOWN LANGUAGE SELECTION";
        };

        return compilationOutput;
    }

    private String compileUserCodePython3(PlaygroundCode playgroundCode) {

        return dockerPlaygroundPython3.compile(playgroundCode);
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
