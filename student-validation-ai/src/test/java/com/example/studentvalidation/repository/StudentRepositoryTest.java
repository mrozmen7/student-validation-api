package com.example.studentvalidation.repository;

import com.example.studentvalidation.domain.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository integration tests.
 *
 * @DataJpaTest (Spring Boot 4.x) loads only the JPA slice: entities, repositories,
 * and JPA/DataSource auto-configuration. No web layer, no services.
 *
 * Testcontainers starts a real PostgreSQL container and wires it in via
 * @DynamicPropertySource so tests never touch the developer's local database.
 *
 * ddl-auto=create-drop is set explicitly to ensure the schema is created fresh
 * for each test run and dropped on teardown.
 */
@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Testcontainers
class StudentRepositoryTest {

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
    private StudentRepository studentRepository;

    // ── existsByEmail ─────────────────────────────────────────────────────────

    @Test
    void existsByEmail_shouldReturnTrue_whenStudentWithEmailExists() {
        studentRepository.save(buildStudent("ali@example.com", "12345678901"));

        assertThat(studentRepository.existsByEmail("ali@example.com")).isTrue();
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenNoStudentWithEmail() {
        assertThat(studentRepository.existsByEmail("ghost@example.com")).isFalse();
    }

    // ── existsByTckn ─────────────────────────────────────────────────────────

    @Test
    void existsByTckn_shouldReturnTrue_whenStudentWithTcknExists() {
        studentRepository.save(buildStudent("veli@example.com", "98765432100"));

        assertThat(studentRepository.existsByTckn("98765432100")).isTrue();
    }

    @Test
    void existsByTckn_shouldReturnFalse_whenNoStudentWithTckn() {
        assertThat(studentRepository.existsByTckn("00000000000")).isFalse();
    }

    // ── existsByEmailAndIdNot (update-path uniqueness check) ──────────────────

    @Test
    void existsByEmailAndIdNot_shouldReturnFalse_whenEmailBelongsToSameStudent() {
        Student saved = studentRepository.save(buildStudent("ali@example.com", "12345678901"));

        // same student updating their own email → must not be treated as a conflict
        assertThat(studentRepository.existsByEmailAndIdNot("ali@example.com", saved.getId()))
                .isFalse();
    }

    @Test
    void existsByEmailAndIdNot_shouldReturnTrue_whenEmailBelongsToDifferentStudent() {
        studentRepository.save(buildStudent("ali@example.com", "12345678901"));
        Student other = studentRepository.save(buildStudent("veli@example.com", "98765432100"));

        // "other" tries to use ali's email → conflict
        assertThat(studentRepository.existsByEmailAndIdNot("ali@example.com", other.getId()))
                .isTrue();
    }

    // ── existsByTcknAndIdNot (update-path uniqueness check) ───────────────────

    @Test
    void existsByTcknAndIdNot_shouldReturnFalse_whenTcknBelongsToSameStudent() {
        Student saved = studentRepository.save(buildStudent("ali@example.com", "12345678901"));

        assertThat(studentRepository.existsByTcknAndIdNot("12345678901", saved.getId()))
                .isFalse();
    }

    @Test
    void existsByTcknAndIdNot_shouldReturnTrue_whenTcknBelongsToDifferentStudent() {
        studentRepository.save(buildStudent("ali@example.com", "12345678901"));
        Student other = studentRepository.save(buildStudent("veli@example.com", "98765432100"));

        assertThat(studentRepository.existsByTcknAndIdNot("12345678901", other.getId()))
                .isTrue();
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Student buildStudent(String email, String tckn) {
        Student student = new Student();
        student.setFirstName("Ali");
        student.setLastName("Veli");
        student.setBirthDate(LocalDate.of(2000, 1, 1));
        student.setEmail(email);
        student.setTckn(tckn);
        return student;
    }
}
