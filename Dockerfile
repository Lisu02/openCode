# Użyj obrazu OpenJDK jako bazowego
FROM eclipse-temurin:17-jdk-alpine

# Ustaw katalog roboczy
WORKDIR /app

# Skopiuj plik JAR aplikacji do obrazu
COPY target/openCode-0.0.9-SNAPSHOT.jar app.jar

# Eksponuj port (zgodny z ustawionym w aplikacji Spring Boot)
EXPOSE 8080

# Ustaw polecenie uruchamiające aplikację
ENTRYPOINT ["java", "-jar", "app.jar"]
