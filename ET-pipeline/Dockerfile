# 1단계: 빌드 단계
FROM gradle:8.12.1-jdk17 AS builder
WORKDIR /workspace
COPY . ./

RUN gradle :ET-pipeline:clean :ET-pipeline:build --no-daemon

# 2단계: 실행 단계
FROM openjdk:17-slim
WORKDIR /app
COPY --from=builder /workspace/ET-pipeline/build/libs/*.jar app.jar

EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]