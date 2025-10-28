FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY gradlew ./
COPY gradle ./gradle

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew :config:clean :config:bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/config-server/build/libs/*.jar app.jar
ENTRYPOINT ["java","-XX:+UseContainerSupport","-Xmx512m","-jar","app.jar"]
