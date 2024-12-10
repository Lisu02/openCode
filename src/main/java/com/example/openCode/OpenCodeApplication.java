package com.example.openCode;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class OpenCodeApplication {

	private static final Dotenv DOTENV = Dotenv.load();


	public static void main(String[] args) {
		System.out.println("Aplikacja uruchamianie...");
		System.out.println(DOTENV.get("DB_HOST"));
		System.out.println(DOTENV.get("DB_PORT"));
		System.out.println(DOTENV.get("DB_NAME"));
		System.out.println(DOTENV.get("DB_USER"));
		System.out.println(DOTENV.get("DB_PASSWORD"));
//		if (System.getProperty("DB_HOST") == null) {
//			System.setProperty("DB_HOST", DOTENV.get("DB_HOST"));
//			System.setProperty("DB_PORT", DOTENV.get("DB_PORT"));
//			System.setProperty("DB_NAME", DOTENV.get("DB_NAME"));
//			System.setProperty("DB_USER", DOTENV.get("DB_USER"));
//			System.setProperty("DB_PASSWORD", DOTENV.get("DB_PASSWORD"));
//		}
		SpringApplication.run(OpenCodeApplication.class, args);
	}


}
