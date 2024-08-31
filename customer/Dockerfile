# Use a base image with Java 17
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/customer-service-0.0.1-SNAPSHOT.jar customer-service.jar

# Expose the port that the Customer microservice runs on
EXPOSE 8081

# Run the JAR file
ENTRYPOINT ["java", "-jar", "customer-service.jar"]