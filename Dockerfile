# Java 버전 명시
FROM openjdk:17
#ARG JAR_FILE=/build/libs/*.jar
COPY app.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]