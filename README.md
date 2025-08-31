# Training Hours Service

This is a modular Java 21 application built using **Spring Boot**. It manages trainer monthly hours using Spring dependency injection, annotations, and Mongo db database.

## Features

- Manage Trainer hour records in Mongo db
- Modular architecture following **SOLID**, **KISS**, and **DRY** principles
- **REST** controllers
- **Spring Security** configured using JWT-tokens from the main microservice
- **Swagger UI** plugged in
- Structured logging at multiple levels (`INFO`, `WARNING`, `SEVERE`)
- Unit tests with **JUnit 5** and **Mockito**
- RestTemplate for microservice communication
- Eureka for service discovery


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
| Microservice API  | RestTemplate              |
| Discovery service | Eureka                    |

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
│   │   └── service/         # Business logic layer
│   └── resources/
│       ├── application.yaml 
│       └── logback.xml
│           
└── test/
    └── java/org.example.trainingapp/
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

### 3. Run eureka service 

### 4. Run the application 

```bash
./gradlew bootRun
```

### 6. Swagger UI is available at:

```bash
http://localhost:8081/swagger-ui/index.html
```

---

## Running Unit Tests

```bash
./gradlew test
```

Test coverage includes:
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

### Task implementation for module 7:

**Based on the codebase created during the previous module, implement follow functionality:**

1. Implement separate Spring boot Application (Microservice).
```
solution: training-hours-service
```

2. Application should implement REST endpoint to accept trainer's workload.
```
solution: TrainerMonthlyHoursController.updateTrainingHours(), dto class is TrainingUpdateRequest
```

3. Implement service function corresponding to mentioned below REST endpoint. Service should calculate as in-memory 
saved structure (in memory DB) trainer's monthly summary of the provided trainings.
```
solution: class TrainingMonthlyHours
```

4. Implement discovery module according to guide Eureka Discovery Service.
```
solution: separate eureka-service, discovery configured in application.yaml 
```

5. Implement Authorization − Bearer token for Microservices integration Use JWT token implementation.
```
solution: AuthTokenFilter + JwtTokenUtil
```

6. Two levels of logging should be implemented - transactions and each operation transaction level - which endpoint was 
called, which request came and the service response - 200 or error and response message + at this level, a transactionId 
is generated, by which you can track all operations for this transaction the same transactionId can later be passed 
to downstream services.
```
solution: classes TransactionLoggingAspect + RestLoggingFilter + TransactionIdFilter
```


**Notes:**
1. For REST API implementation use second level of Richardson maturity model.
```
solution: URIs are resource-oriented, HTTP methods have correct semantics, response statuses are correct.
```

2. In addition, when requesting the number of training hours from any of the trainers in a particular month, the 
microservice retrieves this data from the database and returns it to the requester.
```
solution: TrainerMonthlyHoursController.getTrainerHours()
```

---

## Author

Aleksei Pereverzev  
Developed as part of a Microservices learning module.


