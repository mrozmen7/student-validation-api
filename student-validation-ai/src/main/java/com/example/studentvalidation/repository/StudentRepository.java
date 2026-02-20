package com.example.studentvalidation.repository;

import com.example.studentvalidation.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmail(String email);

    boolean existsByTckn(String tckn);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByTcknAndIdNot(String tckn, Long id);
}
