package com.example.openCode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class OpenCodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenCodeApplication.class, args);
	}

}
