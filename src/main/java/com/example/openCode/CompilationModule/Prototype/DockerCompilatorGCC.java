package com.example.openCode.CompilationModule.Prototype;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DockerCompilatorGCC {

    private static final Logger log = LoggerFactory.getLogger(DockerCompilatorGCC.class);
    DockerClient dockerClient = DockerConfiguration.getDockerClientInstance();
    //TODO:Sprawdzanie id kontenera po liscie
    String gccContainerId = ContainerIdList.getGccContainerId();

    private String compileComand = "gcc -o /tmp/hello /tmp/hello.c 2>&1 >/tmp/compile_output.txt";

    private boolean isContainerRunning(){
        InspectContainerResponse inspectResponse =  dockerClient.inspectContainerCmd(gccContainerId).exec();
        return Boolean.TRUE.equals(inspectResponse.getState().getRunning());
    }

    public String compile(String sourceCode){

        if(!isContainerRunning()){
            dockerClient.startContainerCmd(gccContainerId).exec();
            log.atInfo().log("Starting a GCC container: " + gccContainerId);
        }else{
            log.atInfo().log("GCC Container is running no need for starting");
        }



        System.out.println("In compile method starting container");
        System.out.println(dockerClient.listContainersCmd().exec());
        ExecCreateCmdResponse execCompileUserCode = dockerClient.execCreateCmd(gccContainerId)
                .withAttachStdout(true)
                .withAttachStdin(true)
                .withCmd("sh", "-c", "echo \"" + sourceCode + "\" > /tmp/hello.c && " + compileComand)
                .exec();
        MyResultCallback callbackCompile = new MyResultCallback();
        dockerClient.execStartCmd(execCompileUserCode.getId()).exec(callbackCompile);
        try{
            callbackCompile.awaitCompletion();
        } catch (InterruptedException e){
            e.printStackTrace();
            log.atError().log("COMPILATION FAILED!");
        }

       // dockerClient.stopContainerCmd(gccContainerId).exec();
        return callbackCompile.getOutput();
    }
    //TODO: add catalogName for userCode recognition
    public String runCode(String catalogName) throws InterruptedException {

        if(!isContainerRunning()){
            dockerClient.startContainerCmd(gccContainerId).exec();
            log.atInfo().log("Starting a GCC container: " + gccContainerId);
        }else{
            log.atInfo().log("GCC Container is running no need for starting");
        }

        ExecCreateCmdResponse execRun = dockerClient.execCreateCmd(gccContainerId)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withCmd("/tmp/./"+ catalogName)
                .exec();

        MyResultCallback callbackRun = new MyResultCallback();

        dockerClient.execStartCmd(execRun.getId()).exec(callbackRun);
        callbackRun.awaitCompletion();
        String output = callbackRun.getOutput();
        System.out.println("Output ->" + output);
        //dockerClient.stopContainerCmd(gccContainerId).exec();
//        try {
//            dockerClient.close();
//        }catch (Exception e){
//            System.out.println("DockerCloseError");
//            e.printStackTrace();
//        }

        return output;
    }
    //Overloaded method
    public String runCode()  {
        String output = "empty";
        try{
            output = runCode("hello");
        } catch (InterruptedException e){
            e.printStackTrace();
            output = "ERROR";
        }
        return output;
    }



}
