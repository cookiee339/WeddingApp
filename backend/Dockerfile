FROM gradle:8.4-jdk21 as build

WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle gradle
COPY gradlew ./
COPY src src

RUN chmod +x gradlew
RUN ./gradlew assemble --no-daemon

FROM openjdk:21-jdk-slim

WORKDIR /app

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/build/libs/*-all.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/api/access/codes || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]