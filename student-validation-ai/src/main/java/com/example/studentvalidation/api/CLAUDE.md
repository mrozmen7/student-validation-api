# api/ — Controller Layer Rules

## Package Purpose
Handles HTTP concerns only: routing, request parsing, response shaping.
Delegates all logic to the service layer.

## REST Conventions
- Base path: `/api/students`
- CRUD mapping: GET `/`, GET `/{id}`, POST `/`, PUT `/{id}`, DELETE `/{id}`
- Use plural nouns; no verbs in paths.
- Always version if breaking changes are introduced: `/api/v2/students`

## Request / Response DTOs
- Controllers accept **request DTOs** only — never JPA entities.
- Annotate request DTOs with `@Valid` on method parameters.
- Return **response DTOs** only — never expose entities directly.
- DTO naming: `StudentRequest`, `StudentResponse`.

## HTTP Status Rules
| Operation | Status |
|-----------|--------|
| Create    | 201 Created |
| Read      | 200 OK |
| Update    | 200 OK |
| Delete    | 204 No Content |

## No Business Logic in Controller
- No `if/else` for business rules — that belongs in service.
- No repository injections in controller.
- No manual exception catches for business cases — let `GlobalExceptionHandler` handle them.

## Response Style
- Avoid wrapping every endpoint in `ResponseEntity<>` unless status/header customization is required.
- Prefer consistent response shape via service return types.

## Logging
- Do NOT log request body fields that may contain PII (name, email, TCKN).
- Log at INFO level on entry; DEBUG for internals.

## Model Governance
- Use Sonnet for CRUD controller tasks.
- Opus only if refactoring the entire controller layer or security review.
