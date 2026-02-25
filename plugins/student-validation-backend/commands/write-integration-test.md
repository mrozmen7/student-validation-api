Generate a `@DataJpaTest` + Testcontainers repository integration test for `$ARGUMENTS`.

Follow the **"Spring Boot 4.x Test Conventions"** section in CLAUDE.md exactly — it contains the standard class header, breaking-change notes, and import package names.

## File

`src/test/java/com/example/studentvalidation/repository/<Name>RepositoryTest.java`

## Rules

- One `@Test` per custom `existsBy*` / `findBy*` method: happy path + not-found path.
- Private `build<Entity>(...)` fixture helper — never inline raw field sets in tests.
- No mocking. Real DB only — Testcontainers handles it.
- Skip inherited `JpaRepository` methods (save, findById, delete).
- Test names: `method_shouldX_whenY`.

Confirm with: `./mvnw test -Dtest=<Name>RepositoryTest`
