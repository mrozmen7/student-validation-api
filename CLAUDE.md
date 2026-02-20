# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## AI Governance & Model Usage Policy

### Default Model
- All daily development tasks MUST use Sonnet.
- Sonnet is the default model for CRUD, DTO, Controller, Service logic, validation, and tests.

### Opus Usage Policy
Opus may ONLY be used for: architecture design decisions, large refactoring, deep debugging, or security review. Opus usage must be intentional and temporary.

### Agent Discipline
- Do NOT spawn multiple agents unless necessary.
- Avoid recursive or repeated orchestration patterns.
- Avoid unnecessary long prompts. Use fresh sessions when context grows large.

## Build & Run Commands

All commands run from `student-validation-ai/` directory (where `pom.xml` resides):

```bash
# Build
./mvnw clean package

# Run application
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=ClassName

# Run a single test method
./mvnw test -Dtest=ClassName#methodName
```

## Architecture

Spring Boot 4.0.3 / Java 21 REST API with PostgreSQL. Strict layered architecture — dependencies only flow downward:

```
Controller (api/) → Service (service/) → Repository → Entity (domain/) → PostgreSQL
                         ↑ uses ↓
                      Mapper (mapper/)
                      DTO (dto/)
                      Error (error/)
```

Base package: `com.example.studentvalidation`

Key dependencies: Spring Web MVC, Spring Data JPA, Spring Validation, PostgreSQL driver, Lombok, **MapStruct 1.6.3**.

## Package Structure

| Package | Contents |
|---------|----------|
| `api/` | REST controllers — HTTP routing only, no business logic |
| `service/` | Interface + `Impl` — all business logic lives here |
| `repository/` | Spring Data JPA interfaces |
| `domain/` | JPA entities — no business logic |
| `dto/` | `StudentRequest` (input) and `StudentResponse` (output) |
| `mapper/` | MapStruct interfaces for DTO ↔ Entity conversion |
| `error/` | `GlobalExceptionHandler`, `ApiError`, custom exceptions |

## Configuration

`src/main/resources/application.yml` — PostgreSQL datasource is already configured (`localhost:5432/student_validation`). JPA is set to `ddl-auto: update` with SQL logging enabled.

## Conventions

### Lombok on Domain vs DTO
- **Entities:** use `@Getter @Setter @NoArgsConstructor` — avoid `@Data` (causes JPA `equals`/`hashCode` issues).
- **Request DTOs:** use `@Data`.
- **Response DTOs:** use `@Getter @Builder` (read-only, constructed via builder).

### MapStruct Mapper Pattern
Mappers are `@Mapper(componentModel = "spring")` interfaces — MapStruct generates the implementation at compile time. Always ignore `id`, `createdAt`, and `updatedAt` on `toEntity` and `updateEntity`. Use `NullValuePropertyMappingStrategy.IGNORE` on update methods to preserve existing values.

### Two-Tier Validation
1. **Input validation (Controller):** `@Valid` on request DTO → Bean Validation annotations (`@NotBlank`, `@Email`, `@Pattern`, `@Past`, etc.) → 400 Bad Request with field-level messages.
2. **Business validation (Service):** Custom checks (age 18–65, duplicate email/TCKN) → throw `BusinessRuleException` → 422 Unprocessable Entity.

### Exception → HTTP Status Mapping
| Exception | Status |
|-----------|--------|
| `MethodArgumentNotValidException` | 400 |
| `StudentNotFoundException` | 404 |
| `BusinessRuleException` | 422 |
| `Exception` (catch-all) | 500 |

All error responses use `ApiError` with a unique `traceId` (UUID) and ISO-8601 timestamp.

### Transaction Management
- Class-level `@Transactional` on `ServiceImpl` covers write operations.
- Read methods override with `@Transactional(readOnly = true)`.

### Repository Uniqueness Checks
Use `existsBy*` methods returning `boolean` — never let the DB unique constraint be the first line of defense. Service translates boolean `true` → `BusinessRuleException`. Update paths use `existsByEmailAndIdNot` / `existsByTcknAndIdNot` to allow unchanged values.

### REST Endpoint HTTP Statuses
| Method | Status |
|--------|--------|
| POST `/api/students` | 201 Created |
| GET `/api/students` | 200 OK |
| GET `/api/students/{id}` | 200 OK / 404 |
| PUT `/api/students/{id}` | 200 OK / 404 / 422 |
| DELETE `/api/students/{id}` | 204 No Content / 404 |
