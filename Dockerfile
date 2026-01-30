# Runtime only image
FROM eclipse-temurin:21-jre
LABEL org.opencontainers.image.source="https://github.com/jp-dev273/mutant-detector"

# Add user for application-scope
RUN useradd -r mutant-detector

WORKDIR /app

COPY target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod

USER mutant-detector

ENTRYPOINT ["java", "-jar", "app.jar"]
