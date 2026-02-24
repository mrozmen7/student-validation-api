Generate a @DataJpaTest + Testcontainers PostgreSQL integration test for the repository named **$ARGUMENTS**.

## Instructions

Read `CLAUDE.md` and `MEMORY.md` first to confirm the Spring Boot 4.x test conventions, then generate:

### File — `src/test/java/com/example/studentvalidation/repository/<Name>RepositoryTest.java`

Template:

```java
package com.example.studentvalidation.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Testcontainers
class <Name>RepositoryTest {

    @Container
    static final PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16");

    @DynamicPropertySource
    static void overrideDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private <Name>Repository repository;

    // TODO: add one test per custom repository method (existsBy*, findBy*, etc.)
}
```

### Coverage rules
- One `@Test` per custom repository method — happy path + not-found path.
- Use a private `build<Entity>(...)` helper to construct test fixtures.
- No mocking — this is a real DB test; let Spring + Testcontainers wire everything.
- Do not test standard CRUD inherited from `JpaRepository` (save, findById, delete).

### Spring Boot 4.x reminders
- `@DataJpaTest` lives in `org.springframework.boot.data.jpa.test.autoconfigure` (separate module).
- `PostgreSQLContainer` has no generic type parameter in Testcontainers 2.x.
- `@AutoConfigureTestDatabase(replace=NONE)` is removed — use `@DynamicPropertySource` instead.

After generating, confirm: "Integration test written. Run `./mvnw test -Dtest=<Name>RepositoryTest` to verify."
