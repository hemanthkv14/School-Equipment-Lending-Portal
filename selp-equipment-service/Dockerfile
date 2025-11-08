# selp-equipment-service/Dockerfile

# --- STAGE 1: Build the Application ---
FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app
# Copy the pom.xml and dependency files first to leverage Docker layer caching
COPY pom.xml .
# Note: Copying the wrapper files is necessary if you use ./mvnw (Maven Wrapper)
COPY .mvn .mvn
COPY mvnw .

# Copy the source code
COPY src src

# Run the build command inside the container
# This is where "./mvnw clean package" is automated
RUN ./mvnw clean package -DskipTests

# --- STAGE 2: Create the Final Runtime Image ---
# Use a lightweight JRE image for the final production image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

# Copy the JAR artifact from the build stage
# Spring Boot creates the JAR with a specific name, copy it directly
COPY --from=build /app/target/*.jar app.jar

# Expose the application port (if not already defined in Spring config)
EXPOSE 8080

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]