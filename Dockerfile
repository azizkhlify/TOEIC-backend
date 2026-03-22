FROM gradle:8.12-jdk21 AS build

WORKDIR /app
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY src ./src

RUN gradle installDist --no-daemon

FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app
COPY --from=build /app/build/install/toeic-backend ./

EXPOSE 8080
CMD ["./bin/toeic-backend"]
