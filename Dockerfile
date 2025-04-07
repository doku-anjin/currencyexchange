FROM maven:3.9.3-amazoncorretto-17 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies separately to leverage Docker cache
RUN mvn dependency:go-offline -B
COPY src ./src
# Build and package the application
RUN mvn package -DskipTests

FROM amazoncorretto:17
WORKDIR /app
# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar
# Expose application port
EXPOSE 8080
# Set JVM options and run the application
ENTRYPOINT ["java", "-jar", "app.jar"]