FROM gradle:8.12.1-jdk17 AS builder
WORKDIR /workspace
COPY . ./

RUN gradle :ET-core:clean :ET-core:build --no-daemon

FROM openjdk:17-slim
WORKDIR /app
COPY --from=builder /workspace/ET-core/build/libs/*.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]