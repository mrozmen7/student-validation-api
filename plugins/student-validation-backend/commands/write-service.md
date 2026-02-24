Generate a Spring Boot 4.x service following the project's layered architecture.

## Instructions

Read `CLAUDE.md` first to confirm conventions, then generate the following for the entity named **$ARGUMENTS**:

### 1. Service interface — `service/$ARGUMENTSService.java`

```
package com.example.studentvalidation.service;

public interface <Name>Service {
    <Response> create(<Request> request);
    List<<Response>> findAll();
    <Response> findById(Long id);
    <Response> update(Long id, <Request> request);
    void delete(Long id);
}
```

### 2. Service implementation — `service/<Name>ServiceImpl.java`

Rules:
- `@Service @Transactional` at class level.
- `@Transactional(readOnly = true)` on `findAll` and `findById`.
- Constructor injection only — no `@Autowired` fields.
- Business validation before any persistence call:
  - Duplicate checks via `existsBy*` repository methods → throw `BusinessRuleException`.
  - Age / domain rules → throw `BusinessRuleException`.
- `findById` wraps `repository.findById(...).orElseThrow(EntityNotFoundException::new)`.
- All write paths go through the MapStruct mapper (`toEntity`, `updateEntity`).

### Output rules
- Follow the exact package structure in `CLAUDE.md`.
- Use Lombok `@RequiredArgsConstructor` on the impl class.
- Do not add comments unless logic is non-obvious.
- Do not generate the controller, repository, or entity — service layer only.
- After generating, confirm: "Service skeleton written. Run `./mvnw -DskipTests compile` to verify."
