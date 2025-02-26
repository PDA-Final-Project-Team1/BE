FROM openjdk:17-alpine

VOLUME /tmp

EXPOSE 8761

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} eureka.jar

# 컨테이너 시작 시 JAR 파일을 실행
ENTRYPOINT ["java", "-jar", "/eureka.jar"]