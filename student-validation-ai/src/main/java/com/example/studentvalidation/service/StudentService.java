package com.example.studentvalidation.service;

import com.example.studentvalidation.dto.StudentRequest;
import com.example.studentvalidation.dto.StudentResponse;

import java.util.List;

public interface StudentService {

    StudentResponse create(StudentRequest request);

    List<StudentResponse> findAll();

    StudentResponse findById(Long id);

    StudentResponse update(Long id, StudentRequest request);

    void delete(Long id);
}
