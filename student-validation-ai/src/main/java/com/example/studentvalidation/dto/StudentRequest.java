package com.example.studentvalidation.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequest {

    @NotBlank
    @Size(min = 2, max = 50)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50)
    private String lastName;

    @NotNull
    @Past
    private LocalDate birthDate;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "\\d{11}")
    private String tckn;
}
