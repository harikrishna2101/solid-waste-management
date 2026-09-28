# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build the application
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copy the built jar file
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 9035

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
