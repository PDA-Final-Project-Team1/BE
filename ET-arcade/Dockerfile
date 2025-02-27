FROM gradle:8.12.1-jdk17 AS builder
WORKDIR /workspace
COPY . ./

RUN gradle :ET-arcade:clean :ET-arcade:build --no-daemon

FROM openjdk:17-slim
WORKDIR /app
COPY --from=builder /workspace/ET-arcade/build/libs/*.jar app.jar

EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]