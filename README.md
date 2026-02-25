# student-validation-api

Spring Boot 4.0.3 / Java 21 REST API for student registration with full validation, layered architecture, and Testcontainers integration tests.

![CI](https://github.com/mrozmen7/student-validation-api/actions/workflows/ci.yml/badge.svg)

## Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 4.0.3 / Spring Framework 7.0.5 |
| Language | Java 21 |
| Persistence | Spring Data JPA + PostgreSQL 16 |
| Mapping | MapStruct 1.6.3 |
| Validation | Bean Validation + custom `BusinessRuleException` |
| API Docs | springdoc-openapi 3.0.1 (`/swagger-ui.html`) |
| Tests | JUnit 5, Mockito, Testcontainers 2.x |

## Quick start

```bash
# From student-validation-ai/
./mvnw spring-boot:run      # requires PostgreSQL on localhost:5432
./mvnw test                 # all tests — Testcontainers handles the DB
```

## How we use Claude Code

See [`CLAUDE.md`](CLAUDE.md) for all coding conventions.
See [`MCP-GUIDE.md`](MCP-GUIDE.md) for how to start Claude in the right mode.

| Task | Command |
|------|---------|
| Daily development | `claude --mcp-config .mcp/coding.json --strict-mcp-config` |
| Writing/debugging tests | `claude --mcp-config .mcp/testing.json --strict-mcp-config` |
| Architecture research | `claude --mcp-config .mcp/research.json --strict-mcp-config` |
| Browser automation | `claude --mcp-config .mcp/browser.json --strict-mcp-config` |

### Pre-commit hook

Runs `./mvnw test` before every commit and blocks on failure.

```bash
# Enable once per developer clone
git config core.hooksPath .githooks
```

### Slash commands

| Command | Purpose |
|---------|---------|
| `/write-service` | Generate service interface + impl skeleton |
| `/write-integration-test` | Generate `@DataJpaTest` + Testcontainers template |
| `/enterprise-commit` | Generate Conventional Commit message with approval step |

## CI

GitHub Actions runs on every push and PR to `main`. See [`.github/workflows/ci.yml`](.github/workflows/ci.yml).
Testcontainers uses Docker, which is pre-installed on `ubuntu-latest`.
