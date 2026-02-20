# error/ — Error Handling Rules

## Package Purpose
Centralises all exception definitions and HTTP error mapping.
No business logic lives here — only exception types and a global handler.

## Standard ApiError Structure
Every error response MUST conform to:
```json
{
  "traceId": "uuid",
  "timestamp": "ISO-8601",
  "status": 422,
  "errors": [
    { "field": "age", "message": "must be between 18 and 65" }
  ]
}
```
- `traceId` is generated per request (MDC or UUID).
- `errors` is a list to support multi-field validation failures.

## Error-to-HTTP Mapping
| Exception / Cause              | HTTP Status |
|--------------------------------|-------------|
| `MethodArgumentNotValidException` | 400 Bad Request |
| `StudentNotFoundException`     | 404 Not Found   |
| `BusinessRuleException`        | 422 Unprocessable Entity |
| Uncaught `Exception`           | 500 Internal Server Error |

## GlobalExceptionHandler Conventions
- Annotate with `@RestControllerAdvice`.
- One `@ExceptionHandler` method per exception category.
- Return `ApiError` in all handlers — no plain strings.
- Log with `traceId` at WARN (4xx) or ERROR (5xx); never log stack trace to HTTP response.

## Validation Error Handling (400)
- Extract field errors from `BindingResult`.
- Map each `FieldError` to `{ field, message }` in the `errors` list.
- Do not expose internal class names or stack traces.

## Security
- No stack traces in response body — ever.
- Sanitise exception messages before including in response.
- Log full stack trace server-side only, tagged with `traceId`.
