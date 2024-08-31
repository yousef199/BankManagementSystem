# Use a base image with Java 17
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/account-1.0.0.jar account-service.jar

# Expose the port that the Account microservice runs on
EXPOSE 8082

# Run the JAR file
ENTRYPOINT ["java", "-jar", "account-service.jar"]