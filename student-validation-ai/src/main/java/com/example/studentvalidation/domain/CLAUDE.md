# domain/ — Entity Layer Rules

## Package Purpose
Defines JPA entities that map to database tables.
Entities are the persistence model — they are NOT the API model.

## Critical: Field Modification Policy
- Do NOT add, rename, or remove entity fields without considering DB migration impact.
- Any structural change requires a corresponding Flyway/Liquibase migration script.
- Changing a column constraint (nullable → not-null) is a breaking change — flag it explicitly.

## JPA Annotation Conventions
- Annotate every entity with `@Entity` and `@Table(name = "students")` (explicit table name).
- Primary key: `@Id @GeneratedValue(strategy = GenerationType.IDENTITY)`.
- Use `@Column(nullable = false)` for mandatory fields.
- Unique constraints: declare via `@Table(uniqueConstraints = {...})`, not just `@Column(unique = true)`.

## Uniqueness Rules
- `email` must have a unique constraint.
- `tckn` (if present) must have a unique constraint.
- Enforce at DB level AND catch `DataIntegrityViolationException` in service → translate to 422.

## Keep Entities Lean
- No business logic methods on entity (no `isEligible()`, no age calculations).
- No controller/service annotations (`@Service`, `@Component`, etc.).
- No JSON annotations (`@JsonIgnore`, `@JsonProperty`) — use DTOs for serialisation shape.
- Lombok: use `@Getter @Setter @NoArgsConstructor` — avoid `@Data` (causes issues with JPA equals/hashCode).

## Audit Fields (if applicable)
- Use `@CreationTimestamp` / `@UpdateTimestamp` for `createdAt` / `updatedAt`.
- Do not expose audit fields in response DTOs unless explicitly required.

## Model Governance
- Sonnet for adding new fields or minor entity changes.
- Opus required for entity restructuring, relationship changes, or security review.
