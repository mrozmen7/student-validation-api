Generate a Spring Boot service layer for entity `$ARGUMENTS`. All conventions are in CLAUDE.md — follow them exactly.

## Output

Two files under `src/main/java/com/example/studentvalidation/service/`:

**`<Name>Service.java`** — interface with: `create`, `findAll`, `findById`, `update`, `delete`.

**`<Name>ServiceImpl.java`** — implementation rules:
- `@Service @Transactional @RequiredArgsConstructor` (class level)
- `@Transactional(readOnly = true)` on `findAll` and `findById`
- Constructor injection only
- Uniqueness: `existsBy*` → `BusinessRuleException` (before any save)
- Not found: `orElseThrow(<Name>NotFoundException::new)`
- All conversions via MapStruct mapper (`toEntity`, `updateEntity`)

Scope: service layer only. Do not generate controller, repository, entity, or mapper.

Confirm with: `./mvnw -DskipTests compile`
