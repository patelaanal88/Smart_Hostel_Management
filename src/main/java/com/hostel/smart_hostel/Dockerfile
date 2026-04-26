# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application and skip tests for faster deployment
RUN mvn clean package -DskipTests

# Stage 2: Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Use a wildcard (*) to find the generated JAR file and rename it to app.jar
COPY --from=build /app/target/*.jar app.jar

# Expose the port
EXPOSE 8080

# Run the app with memory limits for Render's Free Tier
ENTRYPOINT ["java", "-Xmx300m", "-jar", "app.jar"]
