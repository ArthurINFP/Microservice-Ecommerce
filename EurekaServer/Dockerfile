# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim
# Set the working directory
WORKDIR /app

# Copy the built Spring Boot application JAR into the Docker container
COPY build/libs/EurekaServer-0.0.1-SNAPSHOT.jar /app/eureka-server.jar

EXPOSE 8888

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "eureka-server.jar"]