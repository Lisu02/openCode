package com.example.openCode.CompilationModule.Controller;

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
@CrossOrigin(origins = "*")
public class PlaygroundController {

    private PlaygroundService playgroundService;
    private final AtomicLong codeID = new AtomicLong(0);


    @Autowired
    public PlaygroundController(PlaygroundService playgroundService) {
        this.playgroundService = playgroundService;
    }


    @PostMapping("/playgroundCompile")
    @Async
    public CompletableFuture<String> playgroundCompilation(@RequestBody String code) {
        PlaygroundCode playgroundCode = new PlaygroundCode(incrementCodeID(), "C", code);
        System.out.println("------PLAYGROUND CONTROLLER----");
        System.out.println(code);
        return CompletableFuture.completedFuture(playgroundService.compilePlaygroundCode(playgroundCode));
    }


    private synchronized long incrementCodeID() {
        return codeID.incrementAndGet();
    }







}