FROM maven:latest AS build
WORKDIR /app
COPY . .
RUN mvn -f ./pom.xml go:offline
RUN mvn -f ./pom.xml package

FROM openjdk:11 AS runtime
COPY target/interview-planning-0.0.1-SNAPSHOT.jar application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]