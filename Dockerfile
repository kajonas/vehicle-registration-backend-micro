# syntax=docker/dockerfile:1

FROM gradle:8.14.2-jdk17 AS builder
WORKDIR /workspace

COPY build.gradle settings.gradle gradle.properties gradlew gradlew.bat ./
COPY gradle ./gradle
COPY src ./src

RUN chmod +x ./gradlew && ./gradlew --no-daemon clean bootJar

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /workspace/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
