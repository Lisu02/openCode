package com.example.openCode.CompilationModule.Service.DockerHandler;

import lombok.Getter;

public class ContainerIdList {

    @Getter
    private static String gccContainerId = DockerConfiguration.getDockerContainerGCC();

    @Getter
    private static String python3ContainerId = DockerConfiguration.getDockerContainerPython3();

}
