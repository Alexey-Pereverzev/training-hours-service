# Training Hours Service

This is a modular Java 21 / Spring Boot 3.5 application built using **Spring Boot**. It manages trainer monthly hours using Spring dependency injection, annotations, Mongo db database, JWT, ActiveMQ and Swagger.

## Features

- Manage Trainer hour records in Mongo db
- Modular architecture following **SOLID**, **KISS**, and **DRY** principles
- **REST** controllers
- **Spring Security** configured using JWT-tokens from the main microservice
- **Swagger UI** plugged in
- Structured logging at multiple levels (`INFO`, `WARNING`, `SEVERE`)
- Unit tests with **JUnit 5** and **Mockito**
- ActiveMQ for microservice communication


---

## Technologies Used

| Component         | Technology                |
|-------------------|---------------------------|
| Language          | Java 21                   |
| Framework         | Spring Boot 3.5           |
| Dependency Tool   | Gradle                    |
| Logging           | Slf4j                     |
| JPA               | Spring Data JPA           |
| Tests             | JUnit 5, Mockito          |
| Configuration     | YAML (`application.yaml`) |
| Security          | Spring Security           |
| Authentication    | JWT-tokens                |
| Storage           | Mongo Db                  |
| API documentation | Swagger                   |
| Messaging         | ActiveMQ                  |
| Containerization  | Docker                    |

---

## Project Structure

```
src/
├── main/
│   ├── java/org.example.trainingapp/
│   │   ├── aspect/          # AOP for transaction logging
│   │   ├── config/          # Spring config classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # DTO for service layer
│   │   ├── entity/          # Domain model
│   │   ├── exception/       # Controller Advice
│   │   ├── filter/          # Security and logging filters
│   │   ├── jwt/             # JWT utils
│   │   ├── repository/      # Mongo repositories
│   │   ├── service/         # Business logic layer
│   │   └── util/            # Validation util
│   └── resources/
│       ├── application.yaml 
│       └── logback.xml
│           
└── test/
    └── java/org.example.trainingapp/
        ├── config/          # Unit tests for JSON converter
        ├── filter/          # Unit tests for filter classes
        ├── jwt/             # Unit tests for jwt util classes
        └── service/         # Unit tests for service classes

```

---

## How to Build and Run

### 1. Clone the repository

```bash
git clone https://github.com/Alexey-Pereverzev/training-hours-service.git
cd training-hours-service
```

### 2. Get RSA public key from the author and put them into /secret folder located in the project root

```bash
public.key
```

### 3. Tests (JUnit):

```bash
./gradlew test
```

### 4. Run the application: run by docker-compose.yml from trainingapp repository

### 5. Swagger UI is available at:

```bash
http://localhost:8081/swagger-ui/index.html
```

### 6. Logs:

```bash
docker compose logs -f training-hours-service   # training-hours-service
docker compose logs -f activemq                 # ActiveMq
docker compose logs -f mongo                    # Mongo
```

---

## Running Unit Tests

```bash
./gradlew test
```

Test coverage includes:
- FixedTypeJsonMessageConverter
- AuthTokenFilter
- RestLoggingFilter
- TransactionIdFilter
- JwtTokenUtil
- TrainingMonthlyHoursServiceImpl

You can find test classes under:
```
src/test/java/org.example.training_hours_service/
```

---

## Example Logs

```
23:05:19.003 [http-nio-8080-exec-2] INFO  o.e.t.a.TransactionLoggingAspect - [1b2ff78a-8369-4f79-98de-b2961b802484] TX-SUCCESS id=1b2ff78a-8369-4f79-98de-b2961b802484
23:05:19.027 [http-nio-8080-exec-2] INFO  o.e.t.config.RestLoggingInterceptor - [] REST-OUT POST /trainingapp/api/trainees/register -> 201
23:05:14.157 [main] WARN  org.hibernate.orm.deprecation - [] HHH90000025: PostgreSQLDialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)
ERROR: Critical error 
```

---

## Notes

- Jwt tokens are generated used RSA-encryption in main microservice.
- Passwords and other sensitive data are **never** logged; only usernames, IDs, or non-confidential fields appear in logs.
- 100% test coverage of services and utils.
- Service is getting requests from the main microservice using RestTemplate
- Microservices discovery is implemented with Eureka

### Design Patterns Used
The project incorporates several established design patterns:

- **DTO** – Request and Response DTOs for service layer.
- **Service Layer** – encapsulates business logic through `TraineeService`, `TrainerService`, etc.
- **Builder** – Dto.builder() via Lombok.
- **Singleton** – Spring-managed beans.
- **Exception Handling (Controller Advice)** – Global error handling via ControllerAdvice

---

### Task implementation for module 10:

**Tasks:**

1. Create Dockerfile for Report microservice with disabled integrations. Create Docker images from the files. Run application.
```
solution: Dockerfile. Can run with docker run command.
```

2. Setup network configuration for Docker. Run application with enabled integrations with DB/queue.
```
solution: docker-compose.yml. Run: docker compose up -d --build from /trainingapp folder
```

3. Start a shell in the running Docker containers and check the application logs.
```
solution: docker compose logs -f training_hours_service
```

---

## Author

Aleksei Pereverzev  
Developed as part of a Docker learning module.


