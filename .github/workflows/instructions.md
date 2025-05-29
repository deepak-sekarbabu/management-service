# GitHub Copilot Coding Standards

## Project Context

- Java 21+, Spring Boot 3.5+
- MySQL (Spring Data JPA/Hibernate)
- Maven build
- Layered architecture: Controller → Service → Repository
- RESTful API, DTOs, validation
- Performance: caching (Redis), async processing
- Clean, maintainable, testable code

---

## Coding Guidelines

### Java & Spring Boot

- Use Java 21 features (records, pattern matching, etc.)
- Use Spring Boot annotations: `@RestController`, `@Service`, `@Repository`, `@Entity`
- Prefer constructor injection (`@RequiredArgsConstructor` via Lombok)
- Use JPA annotations for entity mapping
- Use DTOs/Models for persistence and API layers
- Implement global exception handling (`@ControllerAdvice`)
- Add input validation: `@Valid`, `@NotBlank`, `@Size`, etc.

### Database (MySQL)

- Use Spring Data JPA repositories
- Use `@GeneratedValue(strategy = GenerationType.IDENTITY)` for auto-increment primary keys
- Apply indexing/constraints via JPA annotations
- Use Flyway for schema evolution

### REST API

- Structure endpoints as `/api/v1/resource`
- Use `ResponseEntity<T>` for responses
- Return proper HTTP status codes
- Use `@Valid` in request bodies
- Document endpoints with Swagger/OpenAPI

### Performance

- Use `@Cacheable` for frequently accessed reads (Redis)
- Use `@Async` for long-running tasks
- Index important DB fields

### Testing

- Write unit/integration tests for all business logic and API endpoints
- Use JUnit 5, Mockito
- Use `@SpringBootTest` for integration tests
- Mock external dependencies in unit tests

### Code Quality

- Follow Google Java Style (or similar)
- Use Checkstyle, Spotless, or EditorConfig
- Use meaningful names
- Keep methods short and focused
- Avoid code duplication (DRY)

### Documentation

- Use JavaDoc for public classes/methods
- Document API endpoints with Swagger/OpenAPI
- Maintain a README.md with setup/build/run instructions
- Use Markdown for documentation

---

## Additional Notes

- Use Lombok to reduce boilerplate
- Use Flyway for DB migrations
- Ensure security best practices for JWT and sensitive data
- Keep dependencies up to date
