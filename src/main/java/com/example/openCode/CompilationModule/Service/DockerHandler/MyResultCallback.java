package com.example.openCode.CompilationModule.Service.DockerHandler;

import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.ExecStartResultCallback;

public class MyResultCallback extends ExecStartResultCallback {

    private StringBuilder logBuilder = new StringBuilder();

    @Override
    public void onNext(Frame item) {
        logBuilder.append(new String(item.getPayload()));
        super.onNext(item);
    }

    public String getOutput() {
        return logBuilder.toString();
    }

}
