package com.example.openCode.CompilationModule.Prototype;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DockerCompilatorGCC {

    DockerClient dockerClient = DockerConfiguration.getDockerClientInstance();
    //TODO:Sprawdzanie id kontenera po liscie
    String gccContainerId = ContainerIdList.getGccContainerId();

    private String compileComand = "gcc -o /tmp/hello /tmp/hello.c 2>&1 >/tmp/compile_output.txt";


    public void compile(String sourceCode){
        MyResultCallback myResultCallback = new MyResultCallback();
        dockerClient.startContainerCmd(gccContainerId).exec();



        System.out.println("In compile method starting container");
        System.out.println(dockerClient.listContainersCmd().exec());
        ExecCreateCmdResponse execCompileUserCode = dockerClient.execCreateCmd(gccContainerId)
                .withAttachStdout(true)
                .withAttachStdin(true)
                .withCmd("sh", "-c", "echo \"" + sourceCode + "\" > /tmp/hello.c && " + compileComand)
                .exec();
        MyResultCallback callbackCompile = new MyResultCallback();
        dockerClient.execStartCmd(execCompileUserCode.getId()).exec(callbackCompile);

        dockerClient.stopContainerCmd(gccContainerId).exec();

    }
    //TODO: add catalogName for userCode recognition
    public String runCode(String catalogName){

        ExecCreateCmdResponse execRun = dockerClient.execCreateCmd(gccContainerId)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withCmd("/tmp./"+ catalogName)
                .exec();

        MyResultCallback callbackRun = new MyResultCallback();

        dockerClient.execStartCmd(execRun.getId()).exec(callbackRun);

        String output = callbackRun.getOutput();
        System.out.println(output);
        dockerClient.stopContainerCmd(gccContainerId).exec();
        try {
            dockerClient.close();
        }catch (Exception e){
            System.out.println("DockerCloseError");
            e.printStackTrace();

        }

        return output;
    }
    //Overloaded method
    public String runCode(){
        return runCode("hello");
    }



}
