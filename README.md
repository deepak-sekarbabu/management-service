# Queue Management System for Healthcare Clinics

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/b88e7facd5c247e69abfacc23128c54f)](https://app.codacy.com/gh/deepak-sekarbabu/management-service?utm_source=github.com&utm_medium=referral&utm_content=deepak-sekarbabu/management-service&utm_campaign=Badge_Grade)

This project is a comprehensive queue management system designed for healthcare clinics, enabling efficient scheduling and management of doctor appointments.

The Queue Management System is a Spring Boot application that provides a robust solution for managing doctor appointments, clinic information, and patient queues in healthcare facilities. It offers features such as doctor availability tracking, appointment scheduling, queue generation, and absence management.

Key features include:

- Doctor and clinic information management
- Dynamic queue generation based on doctor availability
- Doctor absence tracking and management
- RESTful API for easy integration with front-end applications
- Automated time slot generation for efficient scheduling

The system is built with scalability and performance in mind, utilizing Spring Boot's powerful features and a MySQL database for data persistence. It's designed to streamline the appointment process, reduce wait times, and improve overall patient experience in healthcare settings.

## Repository Structure

- `src/main/java/com/deepak/management/`: Root package for the application
  - `config/`: Configuration classes
  - `controller/`: REST controllers for handling HTTP requests
  - `exception/`: Custom exception classes and global exception handler
  - `model/`: Data models and DTOs
  - `queue/`: Queue management specific classes
    - `controller/`: Controllers for queue-related operations
    - `jobs/`: Scheduled jobs for queue management
    - `model/`: Queue-specific data models
    - `service/`: Services for queue operations
  - `repository/`: Data access layer interfaces
  - `service/`: Business logic layer
  - `utils/`: Utility classes

Key Files:

- `pom.xml`: Maven project configuration file
- `Dockerfile`: Docker configuration for containerization
- `compose.yaml`: Docker Compose file for local development setup
- `src/main/java/com/deepak/management/ManagementApplication.java`: Main application entry point

## Usage Instructions

### Prerequisites

- Java Development Kit (JDK) 21
- Maven 3.6+
- Docker (optional, for containerized deployment)
- MySQL 8.3.0 (or compatible version)

### Installation

1. Clone the repository:

   ```
   git clone <repository-url>
   cd queue-management-system
   ```

2. Build the project:

   ```
   mvn clean install
   ```

3. Set up the database:
   - Ensure MySQL is running
   - Create a database named `QueueManagement`
   - Update `application.properties` with your database credentials

4. Run the application:

   ```
   java -jar target/management-0.0.1-SNAPSHOT.jar
   ```

### Configuration

The application can be configured through the `application.properties` file. Key configurations include:

- `spring.datasource.url`: Database connection URL
- `spring.datasource.username`: Database username
- `spring.datasource.password`: Database password
- `server.port`: Application server port (default: 8080)

### API Usage

Swagger UI: <https://localhost:8443/swagger-ui/index.html>

The application exposes several RESTful endpoints:

1. Clinic Management:
   - GET `/clinics`: Retrieve all clinics
   - GET `/clinics/{id}`: Get clinic by ID
   - POST `/clinics`: Create a new clinic
   - PUT `/clinics/{id}`: Update clinic information

2. Doctor Management:
   - GET `/doctors`: Retrieve all doctors
   - GET `/doctors/{id}`: Get doctor by ID
   - POST `/doctors`: Add a new doctor
   - PUT `/doctors/{id}`: Update doctor information
   - DELETE `/doctors/{id}`: Delete a doctor

3. Queue Management:
   - GET `/queue-slot`: Get queue slots for a doctor and clinic
   - GET `/queue-slot/generate-time-slots`: Generate time slots for a doctor and clinic

4. Doctor Absence Management:
   - GET `/doctor-absence`: Get all doctor absences
   - POST `/doctor-absence`: Record a new doctor absence
   - PUT `/doctor-absence/{id}`: Update an absence record
   - DELETE `/doctor-absence/{id}`: Delete an absence record

### Testing & Quality

To run the tests:

```
mvn test
```

### Troubleshooting

Common issues and solutions:

1. Database connection issues:
   - Ensure MySQL is running and accessible
   - Verify database credentials in `application.properties`
   - Check for proper JDBC driver configuration

2. Application fails to start:
   - Verify Java version (JDK 21 required)
   - Ensure all dependencies are properly downloaded (run `mvn dependency:resolve`)

3. Queue generation not working:
   - Check logs for any errors in the `TimeSlotJobScheduler` class
   - Verify that doctor and clinic information is properly set up in the database

For debugging:

- Enable debug logging by adding `logging.level.com.deepak.management=DEBUG` to `application.properties`
- Check application logs in the `logs/` directory

## Data Flow

The Queue Management System processes requests through the following flow:

1. Client sends a request to one of the REST endpoints.
2. The appropriate controller receives the request and delegates to the corresponding service.
3. The service layer processes the business logic, interacting with repositories as needed.
4. Repositories communicate with the MySQL database to persist or retrieve data.
5. The service layer returns processed data back to the controller.
6. The controller sends the response back to the client.

```
[Client] <-> [Controller] <-> [Service] <-> [Repository] <-> [Database]
```

For queue generation:

1. `TimeSlotJobScheduler` runs on a scheduled basis.
2. It calls `QueueSlotCreationService` to generate slots for each doctor.
3. Generated slots are saved to the database through `SlotGenerationRepository`.

Important technical considerations:

- Transactions are managed at the service layer to ensure data consistency.
- Caching mechanisms may be implemented to improve performance for frequently accessed data.
- Proper error handling and validation are implemented throughout the flow to ensure data integrity and provide meaningful error messages.

### Prerequisites

- Docker
- Docker Compose

### Deployment Steps

1. Build the Docker image:

   ```
   docker build -t queue-management-system .
   ```

2. Update the `compose.yaml` file with appropriate environment variables.

3. Start the application and database:

   ```
   docker-compose up -d
   ```

4. The application should now be accessible at `http://localhost:8080`.

### Environment Configurations

Ensure the following environment variables are set in your deployment environment:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_JPA_HIBERNATE_DDL_AUTO`
- `SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT`

## Infrastructure

The application's infrastructure is defined in the following files:

- Dockerfile:
  - Base image: openjdk:21-jdk-slim
  - Copies the application JAR and SSL certificates
  - Exposes port 8080
  - Runs the application JAR

- compose.yaml:
  - Defines a MySQL 8.4.5 database service
    - Environment variables for database setup
    - Port mapping: 3306:3306
    - Mounts an initialization SQL script
  - (Commented out) Application service configuration
    - Would build from Dockerfile
    - Environment variables for Spring Boot configuration
    - Port mapping: 8080:8080
    - Depends on the database service

## Authors

- [@deepak-sekarbabu](https://github.com/deepak-sekarbabu)

## License

## Restricted Usage License

This repository is protected by a Restricted Usage License. No part of the content within this repository may be used, reproduced, distributed, or modified in any form without prior written permission from the owner.

For inquiries regarding the use of this repository or its contents, please contact [Deepak Sekarbabu/deepakinmail@gmail.com].

Unauthorized use of this repository or its contents may result in legal action.

Thank you for respecting the terms of this license.
