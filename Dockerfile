# ===============================
# 🔧 Stage 1: Build the App
# ===============================
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy custom library and POM
COPY libs/spring-log-utils-1.0.0.jar /app/libs/
COPY pom.xml .

# Install the custom library to local Maven repo
RUN mvn install:install-file \
    -Dfile=/app/libs/spring-log-utils-1.0.0.jar \
    -DgroupId=com.deepak \
    -DartifactId=spring-log-utils \
    -Dversion=1.0.0 \
    -Dpackaging=jar

# Copy and build source code
COPY src ./src
RUN mvn clean package -DskipTests

# ===============================
# 🛠️ Stage 2: Create Custom JRE
# ===============================
FROM eclipse-temurin:21-jdk-jammy AS jlink
WORKDIR /app

# Generate minimal custom JRE
RUN jlink \
    --no-header-files \
    --no-man-pages \
    --compress=2 \
    --strip-debug \
    --add-modules jdk.unsupported,java.desktop,java.naming,java.management,java.security.jgss,java.security.sasl,jdk.crypto.ec,java.instrument,java.sql,jdk.management \
    --output /customjre


# ===============================
# 🚀 Final Stage: Runtime Image
# ===============================
FROM debian:bullseye-slim
WORKDIR /app

RUN apt-get update && apt-get install -y libzstd1 && rm -rf /var/lib/apt/lists/*
RUN groupadd -r spring && useradd -r -g spring spring
RUN mkdir -p /app/logs/archived && chown -R spring:spring /app

COPY --from=build /app/target/management-1.0.0.jar /app/management.jar
COPY --from=jlink /customjre /opt/java
RUN chown -R spring:spring /opt/java

ENV PATH="/opt/java/bin:$PATH"
USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:+UseStringDeduplication", "-XX:+OptimizeStringConcat", "-jar", "/app/management.jar"]
