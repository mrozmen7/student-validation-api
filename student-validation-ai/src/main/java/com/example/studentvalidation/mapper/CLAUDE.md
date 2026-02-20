# mapper/ — MapStruct Mapper Rules

## Package Purpose
Converts between JPA entities and DTOs.
Mapping logic must NOT leak into controller or service.

## Required Methods (every entity mapper must define)
```java
StudentResponse toResponse(Student entity);
Student toEntity(StudentRequest request);
void updateEntity(StudentRequest request, @MappingTarget Student entity);
```
- `toResponse` — entity → response DTO (read path)
- `toEntity` — request DTO → entity (create path)
- `updateEntity` — applies request DTO fields onto existing entity (update path)

## ID Handling
- NEVER map `id` from request DTO to entity in `toEntity` or `updateEntity`.
- Annotate with `@Mapping(target = "id", ignore = true)` explicitly.

## Null Handling
- Default MapStruct null policy is acceptable for most fields.
- For collections, use `NullValueMappingStrategy.RETURN_DEFAULT` to avoid NPEs.
- Do not silently overwrite non-null entity fields with nulls on partial update — use `@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)` on `updateEntity`.

## DTO Naming Conventions
| Class            | Purpose               |
|------------------|-----------------------|
| `StudentRequest` | Create / update input |
| `StudentResponse`| API response output   |

## What Does NOT Belong Here
- No business logic (no age checks, no validation rules).
- No repository calls.
- No Spring `@Service` or `@Component` annotations — use MapStruct's `@Mapper(componentModel = "spring")`.

## Model Governance
- Sonnet for adding new field mappings.
- Opus only for complex multi-entity mapping refactors.
