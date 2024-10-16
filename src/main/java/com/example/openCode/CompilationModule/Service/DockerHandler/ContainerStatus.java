package com.example.openCode.CompilationModule.Service.DockerHandler;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import lombok.Getter;

public class ContainerStatus {

    //Information about status of the containers

    public ContainerStatus(){}

    public static DockerClient dockerClient = DockerConfiguration.getDockerClientInstance();
    @Getter
    public static String gccContainerId = ContainerIdList.getGccContainerId();

    //TODO: Dynamiczne pobieranie id kontenerów (docelowo w liczbie mnogiej do puli kompilatorów)

    public static boolean isContainerRunning(String containerId) {
        InspectContainerResponse inspectResponse = dockerClient.inspectContainerCmd(containerId).exec();
        return Boolean.TRUE.equals(inspectResponse.getState().getRunning());
    }
}
