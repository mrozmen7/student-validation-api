package com.example.studentvalidation.service;

import com.example.studentvalidation.dto.PagedStudentResponse;
import com.example.studentvalidation.dto.StudentRequest;
import com.example.studentvalidation.dto.StudentResponse;
import org.springframework.data.domain.Pageable;

// email format validasyonu geliştirilecek
public interface StudentService {

    StudentResponse create(StudentRequest request);

    PagedStudentResponse findAll(Pageable pageable);

    StudentResponse findById(Long id);

    StudentResponse update(Long id, StudentRequest request);

    void delete(Long id);
}

