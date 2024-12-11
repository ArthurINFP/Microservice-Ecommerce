# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file produced by the Spring Boot build process to the container
COPY build/libs/APIGateway-0.0.1-SNAPSHOT.jar app.jar

# Expose the port on which the gateway service will run (usually 8080)
EXPOSE 8081

# Define the entry point for the container to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]