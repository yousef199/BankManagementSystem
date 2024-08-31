# Use a base image with Java 17
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/api-gateway-0.0.1-SNAPSHOT.jar api-gateway.jar

# Expose the port that the API Gateway runs on
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]
