FROM gradle:8.12.1-jdk17 AS builder
WORKDIR /workspace
COPY . ./

RUN gradle :ET-api-gateway:clean :ET-api-gateway:build --no-daemon

FROM openjdk:17-slim
WORKDIR /app
COPY --from=builder /workspace/ET-api-gateway/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]