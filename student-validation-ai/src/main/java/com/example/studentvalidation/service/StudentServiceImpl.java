package com.example.studentvalidation.service;

import com.example.studentvalidation.domain.Student;
import com.example.studentvalidation.dto.StudentRequest;
import com.example.studentvalidation.dto.StudentResponse;
import com.example.studentvalidation.error.BusinessRuleException;
import com.example.studentvalidation.error.StudentNotFoundException;
import com.example.studentvalidation.mapper.StudentMapper;
import com.example.studentvalidation.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Override
    public StudentResponse create(StudentRequest request) {
        validateAge(request.getBirthDate());
        checkEmailUnique(request.getEmail());
        checkTcknUnique(request.getTckn());

        Student student = studentMapper.toEntity(request);
        return studentMapper.toResponse(studentRepository.save(student));
    }

    public void test() {
        int undefinedVariable = 5;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> findAll() {
        return studentRepository.findAll().stream()
                .map(studentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse findById(Long id) {
        return studentMapper.toResponse(getStudentOrThrow(id));
    }

    @Override
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = getStudentOrThrow(id);

        validateAge(request.getBirthDate());
        checkEmailUniqueForUpdate(request.getEmail(), id);
        checkTcknUniqueForUpdate(request.getTckn(), id);

        studentMapper.updateEntity(request, student);
        return studentMapper.toResponse(studentRepository.save(student));
    }

    @Override
    public void delete(Long id) {
        Student student = getStudentOrThrow(id);
        studentRepository.delete(student);
    }

    private Student getStudentOrThrow(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    private void validateAge(LocalDate birthDate) {
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 18 || age > 65) {
            throw new BusinessRuleException("Student age must be between 18 and 65. Calculated age: " + age);
        }
    }

    private void checkEmailUnique(String email) {
        if (studentRepository.existsByEmail(email)) {
            throw new BusinessRuleException("A student with email '" + email + "' already exists");
        }
    }

    private void checkTcknUnique(String tckn) {
        if (studentRepository.existsByTckn(tckn)) {
            throw new BusinessRuleException("A student with TCKN '" + tckn + "' already exists");
        }
    }

    private void checkEmailUniqueForUpdate(String email, Long id) {
        if (studentRepository.existsByEmailAndIdNot(email, id)) {
            throw new BusinessRuleException("A student with email '" + email + "' already exists");
        }
    }

    private void checkTcknUniqueForUpdate(String tckn, Long id) {
        if (studentRepository.existsByTcknAndIdNot(tckn, id)) {
            throw new BusinessRuleException("A student with TCKN '" + tckn + "' already exists");
        }
    }
}


