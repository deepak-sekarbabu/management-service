# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the project's JAR file into the container at /app
COPY target/management-0.0.1-SNAPSHOT.jar /app/management.jar

# Copy the certificate and key files into the container at /app/certs
COPY certs/demo.crt /app/certs/demo.crt
COPY certs/demo.key /app/certs/demo.key

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "management.jar"]