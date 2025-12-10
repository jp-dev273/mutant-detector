FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -T 1C -e -B -P prod clean install

# Stage 2: Runtime
FROM eclipse-temurin:21-jre
LABEL org.opencontainers.image.source=https://github.com/jp-dev273/mutant-detector

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
