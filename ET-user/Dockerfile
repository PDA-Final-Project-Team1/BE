FROM gradle:8.12.1-jdk17 AS builder
WORKDIR /workspace
COPY . ./

RUN gradle :ET-user:clean :ET-user:build --no-daemon

FROM openjdk:17-slim
WORKDIR /app
COPY --from=builder /workspace/ET-user/build/libs/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]