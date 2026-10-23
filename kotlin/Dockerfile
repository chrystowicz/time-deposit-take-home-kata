# Build stage: compile and package the Spring Boot jar.
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /workspace

COPY gradlew gradlew.bat ./
COPY gradle ./gradle
COPY settings.gradle.kts build.gradle.kts ./
# Warm the dependency cache as its own layer, so it's reused across builds
# unless build.gradle.kts changes. Not the real build -- failures here don't
# need to fail the image build, the bootJar step below will surface them.
RUN ./gradlew --no-daemon dependencies || true

COPY src ./src
RUN ./gradlew --no-daemon bootJar

# Runtime stage: slim JRE, only the built jar.
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
