
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/*.jar /app/vitals-subscriber.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/vitals-subscriber.jar"]

