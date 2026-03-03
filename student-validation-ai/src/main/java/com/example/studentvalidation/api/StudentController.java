package com.example.studentvalidation.api;

import com.example.studentvalidation.dto.PagedStudentResponse;
import com.example.studentvalidation.dto.StudentRequest;
import com.example.studentvalidation.dto.StudentResponse;
import com.example.studentvalidation.service.StudentService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponse create(@Valid @RequestBody StudentRequest request) {
        return studentService.create(request);
    }

    // TODO: pagination eklenecek
    @GetMapping
    public PagedStudentResponse findAll(@PageableDefault(size = 10) Pageable pageable,
                                        HttpServletResponse response) {
        response.setHeader("Cache-Control", CacheControl.noCache().getHeaderValue());
        return studentService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public StudentResponse findById(@PathVariable Long id, HttpServletResponse response) {
        response.setHeader("Cache-Control",
                CacheControl.maxAge(60, TimeUnit.SECONDS).cachePrivate().getHeaderValue());
        return studentService.findById(id);
    }

    @PutMapping("/{id}")
    public StudentResponse update(@PathVariable Long id, @Valid @RequestBody StudentRequest request,
                                  HttpServletResponse response) {
        response.setHeader("Cache-Control", CacheControl.noStore().getHeaderValue());
        return studentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        studentService.delete(id);
    }
}
