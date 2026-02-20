package com.example.studentvalidation.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class StudentResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String email;
    private String tckn;
}
