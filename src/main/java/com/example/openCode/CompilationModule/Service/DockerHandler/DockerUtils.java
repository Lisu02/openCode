package com.example.openCode.CompilationModule.Service.DockerHandler;

import java.time.Duration;

public class DockerUtils {

    public static int basicTimeoutTime = 2000;

    public static String getTime(String output) {
        int outputTimeSize = output.lastIndexOf("Elapsed (wall clock) time (h:mm:ss or m:ss):");
        int outputTimeStart = output.lastIndexOf("Command being timed:");
        return output.substring(outputTimeSize + 48, outputTimeSize + 48 + 4);
    }

    public static String getMemory(String output) {
        int outputMemorySize = output.lastIndexOf("Minor (reclaiming a frame) page faults:");
        int outputMemoryEnd = output.lastIndexOf("Voluntary context switches");
        return output.substring(outputMemorySize + 40,outputMemoryEnd-2);
    }

    public static String getOnlyCodeOutput(String output) {
        int outputTimeStart = output.lastIndexOf("Command being timed:");
        return output.substring(0,outputTimeStart);
    }

    public static long convertStringTimeToLong(String timeString) {
        double seconds = Double.parseDouble(timeString);
        long milliseconds = (long) (seconds * 1000);
        return milliseconds;
    }

    public static long convertStringMemoryToLong(String memory) {
        return Long.parseLong(memory);
    }

}
