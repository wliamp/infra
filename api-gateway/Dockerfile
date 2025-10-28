FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /app
COPY gradlew ./
COPY gradle ./gradle
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew :gtw:clean :gtw:bootJar --no-daemon
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /app/api-gateway/build/libs/*.jar app.jar
ENTRYPOINT ["java","-XX:+UseContainerSupport","-Xmx512m","-jar","app.jar"]
