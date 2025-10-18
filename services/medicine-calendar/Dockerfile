
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/*.jar /app/medicine-calendar.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app/medicine-calendar.jar"]
