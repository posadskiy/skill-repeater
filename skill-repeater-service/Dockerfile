# === Stage 1: Build the application ===
FROM maven:3.9.12-amazoncorretto-25-alpine AS build
WORKDIR /app

COPY . .

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B clean install -pl skill-repeater-web -am -DskipTests

# === Stage 2: Create the runtime image ===
FROM amazoncorretto:25-alpine-jdk
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/skill-repeater-web/target/skill-repeater-web-*.jar app.jar

# Expose the application port (defined in application.yaml)
EXPOSE 8210

# Run the application
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "-Dmicronaut.environments=${MICRONAUT_ENVIRONMENTS}", "app.jar"]
