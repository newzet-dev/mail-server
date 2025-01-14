FROM eclipse-temurin:17-jdk-alpine
COPY ./build/libs/*SNAPSHOT.jar project.jar
CMD ["java", "-jar", "project.jar"]
