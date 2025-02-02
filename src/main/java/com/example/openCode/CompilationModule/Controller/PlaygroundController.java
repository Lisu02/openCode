package com.example.openCode.CompilationModule.Controller;

import com.example.openCode.CompilationModule.DTO.PlaygroundDTO;
import com.example.openCode.CompilationModule.Model.PlaygroundCode;
import com.example.openCode.CompilationModule.Service.PlaygroundService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class PlaygroundController {

    private PlaygroundService playgroundService;
    private final AtomicLong codeID = new AtomicLong(0);


    @Autowired
    public PlaygroundController(PlaygroundService playgroundService) {
        this.playgroundService = playgroundService;
    }


    @PostMapping("/playgroundCompile/v2")
    public String playgroundCompilation(@RequestBody PlaygroundDTO playgroundDTO) {
        PlaygroundCode playgroundCode = new PlaygroundCode(incrementCodeID(),
                playgroundDTO.getProgrammingLanguage().toUpperCase(),
                playgroundDTO.getCode());
        System.out.println("------PLAYGROUND CONTROLLER----");
        System.out.println(playgroundCode.getCode());
        return playgroundService.compilePlaygroundCode(playgroundCode);
    }



    private synchronized long incrementCodeID() {
        return codeID.incrementAndGet();
    }







}
