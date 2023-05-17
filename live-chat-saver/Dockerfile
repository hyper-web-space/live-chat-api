FROM openjdk:17-jdk-slim AS builder
COPY .. /tmp
WORKDIR /tmp
RUN ./gradlew :live-chat-saver:clean :live-chat-saver:bootJar

FROM openjdk:17-jdk-slim
COPY --from=builder /tmp/live-chat-saver/build/libs/live-chat-saver.jar /app/app.jar
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "./app.jar"]
