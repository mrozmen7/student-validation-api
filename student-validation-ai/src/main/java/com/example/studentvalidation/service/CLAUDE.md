# service/ — Service Layer Rules

## Package Purpose
Contains all business logic, validation rules, and orchestration.
Is the only layer allowed to call repositories.

## Business Rules (MUST enforce)
- Student age must be **between 18 and 65** (inclusive).
- Age is derived from `birthDate`; calculate at service layer, not entity.
- Duplicate email or TCKN must throw a business exception (→ 422).

## Transactions
- Use `@Transactional` on write operations (create, update, delete).
- Read-only queries: annotate with `@Transactional(readOnly = true)`.
- Never catch and swallow `RuntimeException` inside a `@Transactional` method.

## Exception Contract
| Scenario         | Exception to throw          | HTTP result |
|------------------|-----------------------------|-------------|
| Entity not found | `StudentNotFoundException`  | 404         |
| Business rule violation | `BusinessRuleException` | 422      |
| Duplicate field  | `BusinessRuleException`     | 422         |

- Exceptions are defined in `error/`; service only throws them.
- Never throw raw `RuntimeException` or `IllegalArgumentException` from service.

## Return Types
- Service methods return **DTOs or result objects**, not JPA entities.
- Conversion from entity → DTO is done via `mapper/`; call mapper here, not in controller.

## No Cross-Layer Leakage
- No `HttpServletRequest`, `@RequestParam`, or Spring MVC annotations in service.
- No direct JSON/serialization concerns in service.

## Model Governance
- Sonnet for standard CRUD service logic.
- Opus for deep refactoring, concurrency concerns, or security review.
