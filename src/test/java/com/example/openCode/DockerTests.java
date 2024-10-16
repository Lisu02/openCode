package com.example.openCode;

import com.example.openCode.CompilationModule.Service.DockerHandler.ContainerStatus;
import com.example.openCode.CompilationModule.Service.DockerHandler.DockerConfiguration;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


public class DockerTests {

    //To run any tests properly you need to run docker container first

    @Test
    public void gettingContainerId(){
        //DockerConfiguration dockerConfiguration = new DockerConfiguration();
        DockerConfiguration.printDockerContainerList();
    }

    @Test
    public void gettingContainerGCC(){
        //DockerConfiguration.printDockerContainerList();
        System.out.printf("---------GCC---------\n");
        System.out.printf(DockerConfiguration.getDockerContainerGCC() + "\n");
    }
}
