# OpenCode - online docker based compiler
App for compiling and running your C and Python code in a safe environment. Created using Spring Boot, Docker and Java with JWT authentication. (Backend only)

## Features

- Running your code in a playground environment (you can send everything and it will be ran and compiled)
- Creating your own programing tasks (like leetcode)
- Adding test cases to yours programing tasks
- Solving tasks using C or Python

## Installation

_Open code_ requires Java 17, Maven and Docker running on your machine.

To run the project locally you need to...

*remove app service in compose.yaml (requires logging in to pull the openCode-app image)

*create .env file with DB_USER, DB_PASSWORD, DB_PORT for postgres db
```sh
git clone https://github.com/Lisu02/openCode.git
cd openCode
docker-compose up -d
mvn clean package
cd target
java -jar openCode-0.1.0-SNAPSHOT.jar
```

## Used libraries

| Library | README |
| ------ | ------ |
| docker-java | [docker-java/blob/main/docs/README.md][PlDb] |
| io.jsonwebtoken | [jjwt/blob/master/README.adoc][PlGh] |

## Images of the application
*endpoints used in images may be no longer available
![image](https://github.com/user-attachments/assets/fdaa5f64-559b-4776-a4fd-6b4720906387)
![image](https://github.com/user-attachments/assets/b752f41b-90f7-4f20-8650-ae985fb9db00)
![image](https://github.com/user-attachments/assets/d9ae561f-0153-492b-acbb-0066f4653746)



[PlDb]: <https://github.com/docker-java/docker-java/blob/main/docs/README.md>
[PlGh]: <https://github.com/jwtk/jjwt/blob/master/README.adoc>
