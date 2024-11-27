package com.example.openCode;

import com.example.openCode.CompilationModule.Service.DockerHandler.ContainerStatus;
import com.example.openCode.CompilationModule.Service.DockerHandler.DockerConfiguration;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class DockerTests {

    //To run any tests properly you need to run docker container first

    @Test
    public void gettingContainerId(){
        DockerConfiguration.printDockerContainerList();
    }

    @Test
    public void gettingContainerGCC(){
        System.out.printf("---------GCC container id---------\n");
        System.out.printf(DockerConfiguration.getDockerContainerGCC() + "\n");
        //assertThat(DockerConfiguration.getDockerContainerGCC()).isNotNull();
    }
}
