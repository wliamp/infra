# Service Registry/Discovery (Eureka Server)

The service-registry module is a Eureka Server used for registering and discovering microservices in a system. It acts as a central hub allowing service clients to locate each other dynamically, reducing static configuration.

## Reference
* [Eureka Server Documentation](https://docs.spring.io/spring-cloud-netflix/reference/spring-cloud-netflix.html#spring-cloud-eureka-server)
* [Service Registration and Discovery](https://spring.io/guides/gs/service-registration-and-discovery/)

## Requirements
- Java 17+
- Gradle
- Spring Boot 3.x

## Enviroments Setup
#### Dependencies (Gradle - Groovy)
```bash
ext {
    set('springCloudVersion', "2025.0.0")
}

dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"
    }
}
```
```bash
implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-server'
```

#### Configurations (application.properties)
```bash
spring.application.name = service-registry

eureka.client.register-with-eureka = false
eureka.client.fetch-registry = false

eureka.client.server.wait-time-in-ms-when-sync-empty = 30000

# Check expired instances
eureka.client.server.eviction-interval-timer-in-ms = 30000

# Cache update interval
eureka.client.server.response-cache-update-interval-ms = 3000

eureka.client.server.enable-self-preservation = true

# Limit number of instances per app in memory
eureka.client.server.max-in-memory-instances-per-app = 1000

# Self-preservation threshold
eureka.client.server.renewal-percent-threshold = 0.85
```
- `register-with-eureka = false` – Server does not register itself.
- `fetch-registry = false` – Do not pull registry from other Eureka servers.
- `wait-time-in-ms-when-sync-empty = 30000` – Wait 30s at startup if registry is empty; prevents clients from getting empty registry.
- `eviction-interval-timer-in-ms = 30000` – Evict instances that do not send heartbeat every 30s.
- `response-cache-update-interval-ms = 3000` – Update cache every 3s.
- `enable-self-preservation = true` – Protect server from mistakenly removing healthy instances due to temporary network issues.
- `max-in-memory-instances-per-app` – Prevent memory overflow if huge number of services register.
- `renewal-percent-threshold` – If fewer instances renew, server goes into self-preservation mode.

## Run Application

- Gradle Task (CMD):
  gradlew.bat bootRun --args="--server.port=**PORT**"
```bash
gradlew.bat bootRun --args="--server.port=8761"
```
- Java:
  java -jar **`jarFile`** --server.port=**`PORT`**
```bash
java -jar build/libs/registry.jar --server.port=8761
```

## Service Client Usage

#### Dependencies (Gradle - Groovy)
```bash
ext {
    set('springCloudVersion', "2025.0.0")
}

dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"
    }
}
```
```bash
implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
```

#### Configurations (application.properties)
```bash
spring.application.name = service-name
server.port = 8080
# =================== Eureka Server Registration ===================
eureka.client.service-url.defaultZone = http://localhost:8761/eureka
eureka.instance.prefer-ip-address = true
eureka.instance.instance-id = ${spring.application.name}:${server.port}
```
- `eureka.client.service-url.defaultZone` – Specifies the URL of the Eureka Server where this service should register itself.
- `eureka.instance.prefer-ip-address` – Eureka registers the service using its IP address instead of hostname.
- `eureka.instance.instance-id` – Sets a unique identifier for this service instance in the Eureka registry.
