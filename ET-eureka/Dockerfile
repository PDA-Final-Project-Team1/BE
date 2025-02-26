# 1단계: 빌드 단계
FROM gradle:7.6-jdk17 AS builder

WORKDIR /workspace
COPY . ./

# 1) 특정 모듈(ET-eureka)만 빌드
RUN gradle :ET-eureka:clean :ET-eureka:build --no-daemon

# 2단계: 실행 단계
FROM openjdk:17-slim
WORKDIR /app

# 2) 빌드된 JAR 파일 복사 (ET-eureka 모듈)
COPY --from=builder /workspace/ET-eureka/build/libs/*.jar app.jar

# Eureka 서버의 기본 포트 8761 노출
EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]