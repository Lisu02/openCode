package com.example.openCode.CompilationModule.Service.DockerHandler;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

import java.time.Duration;

public class DockerConfiguration {

    static final private DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();

    static private DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .sslConfig(config.getSSLConfig())
            .maxConnections(100)
            .connectionTimeout(Duration.ofSeconds(30))
            .responseTimeout(Duration.ofSeconds(45))
            .build();

    static final private DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);

    public static DockerClient getDockerClientInstance() {
        return dockerClient;
    }

    public static void printDockerContainerList() {

        System.out.println("--------Start of List--------");
        for (Container container : dockerClient.listContainersCmd().exec()) {
            System.out.println("Id: " + container.getId() + " | image: " + container.getImage() + " status: " + container.getStatus());
        }
        System.out.println("--------End of List--------");
    }
}
