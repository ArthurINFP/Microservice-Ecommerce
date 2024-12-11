# Start with an official OpenJDK runtime image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Add the application's JAR file
# Assuming the JAR is built and located in the target directory (change if needed)
COPY build/libs/UserService-0.0.1-SNAPSHOT.jar /app/UserService.jar

# Expose the port that the application will run on
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/UserService.jar"]