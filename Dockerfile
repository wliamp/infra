FROM gradle:9.0-jdk25-alpine AS builder
WORKDIR /app
COPY gradlew ./gradlew
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies --no-daemon || true
ARG SERVICE
COPY ${SERVICE}/src ./src
RUN ./gradlew :${SERVICE}:bootJar --no-daemon
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]
