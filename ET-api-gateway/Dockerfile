FROM openjdk:17-jdk-gslim

VOLUME /tmp

EXPOSE 8080

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} api-gateway.jar

# 컨테이너 시작 시 JAR 파일을 실행
ENTRYPOINT ["java", "-jar", "/api-gateway.jar"]