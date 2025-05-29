# Queue Management System for Healthcare Clinics

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/b88e7facd5c247e69abfacc23128c54f)](https://app.codacy.com/gh/deepak-sekarbabu/management-service?utm_source=github.com&utm_medium=referral&utm_content=deepak-sekarbabu/management-service&utm_campaign=Badge_Grade)

A modern, scalable queue and appointment management system for healthcare clinics, built with Java 21, Spring Boot 3.5+, and MySQL. The system streamlines doctor scheduling, patient queues, and clinic operations with RESTful APIs, robust validation, and automated slot generation.

## Key Features

- Doctor and clinic information management
- Dynamic queue and time slot generation based on doctor availability
- Doctor absence tracking and management
- RESTful API with OpenAPI/Swagger documentation
- Automated, scheduled slot generation (via Spring Scheduler)
- Scalable, layered architecture (Controller → Service → Repository)
- Input validation and global exception handling
- Docker and Docker Compose support for easy deployment

## Technology Stack

- Java 21, Spring Boot 3.5+
- Spring Data JPA (MySQL 8.3+)
- Maven (build & dependency management)
- Lombok (boilerplate reduction)
- Swagger/OpenAPI (API docs)
- Spotless (code formatting)
- JUnit 5 & Mockito (testing)
- Docker, Docker Compose

## Repository Structure

- `src/main/java/com/deepak/management/`
  - `config/` – Configuration classes
  - `controller/` – REST controllers (Clinic, Doctor, Queue, Absence)
  - `exception/` – Custom exceptions & global handler
  - `model/` – Data models & DTOs
  - `queue/` – Queue management (controller, jobs, model, service)
  - `repository/` – Data access layer
  - `service/` – Business logic
  - `utils/` – Utility classes
- `src/main/resources/` – Application properties, SQL, etc.
- `src/test/java/` – Unit & integration tests
- `Dockerfile`, `compose.yaml` – Containerization & orchestration
- `pom.xml` – Maven project configuration
- `docs/flow.mmd` – System flowchart (Mermaid)

## Setup & Usage

### Prerequisites

- Java 21+
- Maven 3.6+
- MySQL 8.3+ (or compatible)
- Docker (optional)

### Installation & Run

```sh
git clone <repository-url>
cd management-service
mvn clean install
```

1. Configure your database in `src/main/resources/application.properties`.
2. Ensure MySQL is running and a database named `QueueManagement` exists.
3. Run the application:

   ```sh
   java -jar target/management-0.0.1-SNAPSHOT.jar
   ```

### Docker Deployment

```sh
docker build -t queue-management-system .
docker-compose up -d
```

- The app will be available at [http://localhost:8080](http://localhost:8080).

### API Documentation

- Swagger UI: [https://localhost:8443/swagger-ui/index.html](https://localhost:8443/swagger-ui/index.html)
- Endpoints cover:
  - Clinic: `/clinics`, `/clinics/{id}`
  - Doctor: `/doctors`, `/doctors/{id}`
  - Queue: `/queue-slot`, `/queue-slot/generate-time-slots`, `/queue/details/{clinicId}/{doctorId}`
  - Doctor Absence: `/doctor-absence`, `/doctor-absence/{id}`

## Testing & Quality

- Run all tests: `mvn test`
- Code style enforced via Spotless (Google Java Format)
- High code coverage and meaningful assertions expected

## Troubleshooting

- **DB connection issues:** Check MySQL is running and credentials in `application.properties`.
- **App fails to start:** Ensure Java 21+ and all Maven dependencies are resolved.
- **Queue generation issues:** Check logs for errors in `TimeSlotJobScheduler` or service classes.

## Data Flow

```mermaid
flowchart TD
    A[🏥 Clinic Management<br/>POST /clinic<br/>Create Clinic] --> B[👨‍⚕️ Doctor Management<br/>POST /doctor<br/>Add Doctor to Clinic]
    B --> C[📅 Doctor Absence Management<br/>POST /doctor-absence<br/>Record Doctor Absence]
    C --> D[⚙️ Queue Slot Service<br/>GET /queue-slot<br/>Fetch Doctor/Clinic Availability]
    D --> E[⏱️ Generate Time Slots<br/>GET /queue-slot/generate-time-slots<br/>Create Slots]
    E --> F[📋 Queue Details<br/>GET /queue/details/{clinicId}/{doctorId}<br/>View Queue Information]
    F --> G[👥 Information Available for Appointment/Registration Booking<br/>Book Slots]
    style A fill:#d9fdd3,stroke:#333,stroke-width:1px
    style B fill:#c3f0f4,stroke:#333,stroke-width:1px
    style C fill:#f9f0c6,stroke:#333,stroke-width:1px
    style D fill:#e0d8f9,stroke:#333,stroke-width:1px
    style E fill:#fce2db,stroke:#333,stroke-width:1px
    style F fill:#e6f7ff,stroke:#333,stroke-width:1px
    style G fill:#d4eaff,stroke:#333,stroke-width:2px
```

- Scheduled jobs auto-generate slots for each doctor/clinic daily.
- See `docs/flow.mmd` for a detailed flowchart.

## Coding Standards

- Java 21 features (records, pattern matching, etc.)
- Constructor injection with Lombok's `@RequiredArgsConstructor`
- JPA for entity mapping, Flyway for migrations (recommended)
- Global exception handling, input validation
- RESTful best practices (status codes, DTOs, OpenAPI docs)
- Code formatting via Spotless
- See `.github/workflows/instructions.md` for full guidelines

## Authors

- [@deepak-sekarbabu](https://github.com/deepak-sekarbabu)

## License

This project is licensed under the BSD 3-Clause License. See the [LICENSE](LICENSE) file for details.

---

For inquiries or permission to use this repository, contact Deepak Sekarbabu at [deepakinmail@gmail.com](mailto:deepakinmail@gmail.com).
