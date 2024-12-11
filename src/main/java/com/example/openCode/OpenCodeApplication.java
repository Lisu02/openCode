package com.example.openCode;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
public class OpenCodeApplication {

	private static final Dotenv DOTENV = Dotenv.configure().ignoreIfMissing().load();

	public static void main(String[] args) {
		System.out.println("Aplikacja uruchamianie...");

		// Pobieranie zmiennych
		String dbHost = getEnvVariable("DB_HOST", "postgres");
		String dbPort = getEnvVariable("DB_PORT", "5432");
		String dbName = getEnvVariable("DB_NAME", "openCodeDB");
		String dbUser = getEnvVariable("DB_USER", "user");
		String dbPassword = getEnvVariable("DB_PASSWORD", "user");

		// debug
		System.out.println("DB_HOST: " + dbHost);
		System.out.println("DB_PORT: " + dbPort);
		System.out.println("DB_NAME: " + dbName);
		System.out.println("DB_USER: " + dbUser);
		System.out.println("DB_PASSWORD: " + dbPassword);

		SpringApplication.run(OpenCodeApplication.class, args);
	}

	//  zmienne środowiskowowe z domyślną wartością
	private static String getEnvVariable(String key, String defaultValue) {
		String value = System.getenv(key); // Pobierz z systemu
		if (value == null) {
			value = DOTENV.get(key); // Pobierz z .env, jeśli brak w systemie
		}
		return value != null ? value : defaultValue;
	}
}

