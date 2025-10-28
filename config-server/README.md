# Spring Cloud Config Server

The config-server module is a Spring Cloud Config Server used for providing externalized configuration for distributed systems. It allows microservices to fetch their configuration properties dynamically from a central place (Git, SVN, or local file system), ensuring consistency across environments.

## Reference
* [Config Server](https://docs.spring.io/spring-cloud-config/reference/server.html)
* [Centralized Configuration](https://spring.io/guides/gs/centralized-configuration/)

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
implementation 'org.springframework.cloud:spring-cloud-config-server'
```

#### Enable Config Server (Main Class)
```bash
@EnableConfigServer // 👈 Enable Spring Cloud Config Server
@SpringBootApplication
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

#### Configurations (application.properties)
```bash
spring.application.name = config-server
server.port = 8888

# =================== Backend Store (Git) ===================
spring.cloud.config.server.git.uri = https://github.com/example/config-repo
spring.cloud.config.server.git.username = my-username
spring.cloud.config.server.git.password = my-pat # 👈 Personal Access Token
spring.cloud.config.server.git.clone-on-start = true
spring.cloud.config.server.git.default-label = main

# Declare search path
spring.cloud.config.server.git.search-paths = common, apps

# =================== Fail Fast ===================
spring.cloud.config.server.bootstrap = true

```
- `spring.cloud.config.server.git.uri` – Location of Git repo storing configuration files.
- `spring.cloud.config.server.git.clone-on-start` – Clone repo immediately when server starts.
- `spring.cloud.config.server.git.default-label` – Default Git branch (e.g., main, master, dev).
- `spring.cloud.config.server.git.search-paths` – Specifies subfolders in the Git repo where configuration files are searched.
- `spring.cloud.config.server.bootstrap = true` – Ensures configuration server loads before application context.

#### Config Repo (Git)
Create a repo contains config files, example:
```css
config-repo/
├── common/
|       ├── application.properties
|       └── logging.properties
└── apps/
        ├── service-a.properties
        └── service-b.properties
```

## Run Application

- Gradle Task (CMD):
```bash
gradlew.bat bootRun
```
- Java:
```bash
java -jar build/libs/config.jar
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
implementation 'org.springframework.cloud:spring-cloud-starter-config'
```

#### Configurations (application.properties)
```bash
spring.application.name = service-name
spring.config.import = optional:configserver:http://localhost:8888
```
- `spring.application.name` – Used to fetch {appName}.properties or {appName}-{profile}.properties from config repo.
- `spring.config.import` – Points to Config Server URL.
