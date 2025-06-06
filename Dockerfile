# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the custom library and pom.xml first
COPY libs/spring-log-utils-1.0.0.jar /app/libs/
COPY pom.xml .

# Install the custom library
RUN mvn install:install-file \
    -Dfile=/app/libs/spring-log-utils-1.0.0.jar \
    -DgroupId=com.deepak \
    -DartifactId=spring-log-utils \
    -Dversion=1.0.0 \
    -Dpackaging=jar

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create a non-root user
RUN groupadd -r spring && useradd -r -g spring spring

# Create logs directory and set permissions
RUN mkdir -p /app/logs && \
    mkdir -p /app/logs/archived && \
    chown -R spring:spring /app/logs

USER spring:spring

# Copy the built jar from build stage
COPY --from=build /app/target/management-1.0.0.jar /app/management.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["sh", "-c", "java -jar management.jar"]