FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/*.jar /app/vitals-publisher.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/vitals-publisher.jar"]
