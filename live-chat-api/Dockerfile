FROM openjdk:17-jdk-slim AS builder
COPY .. /tmp
WORKDIR /tmp
RUN ./gradlew :live-chat-api:clean :live-chat-api:bootJar

FROM openjdk:17-jdk-slim
COPY --from=builder /tmp/live-chat-api/build/libs/live-chat-api.jar /app/app.jar
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "./app.jar"]
