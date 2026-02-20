package com.example.studentvalidation.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(MethodArgumentNotValidException ex) {
        String traceId = UUID.randomUUID().toString();
        Map<String, List<String>> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.computeIfAbsent(fieldError.getField(), k -> new ArrayList<>())
                    .add(fieldError.getDefaultMessage());
        }
        log.warn("[{}] Validation failed: {}", traceId, errors);
        return ApiError.builder()
                .traceId(traceId)
                .timestamp(Instant.now().toString())
                .status(HttpStatus.BAD_REQUEST.value())
                .errors(errors)
                .build();
    }

    @ExceptionHandler(StudentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleStudentNotFoundException(StudentNotFoundException ex) {
        String traceId = UUID.randomUUID().toString();
        log.warn("[{}] Student not found: {}", traceId, ex.getMessage());
        return ApiError.builder()
                .traceId(traceId)
                .timestamp(Instant.now().toString())
                .status(HttpStatus.NOT_FOUND.value())
                .errors(Map.of("message", List.of(ex.getMessage())))
                .build();
    }

    @ExceptionHandler(BusinessRuleException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ApiError handleBusinessRuleException(BusinessRuleException ex) {
        String traceId = UUID.randomUUID().toString();
        log.warn("[{}] Business rule violation: {}", traceId, ex.getMessage());
        return ApiError.builder()
                .traceId(traceId)
                .timestamp(Instant.now().toString())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .errors(Map.of("message", List.of(ex.getMessage())))
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleGenericException(Exception ex) {
        String traceId = UUID.randomUUID().toString();
        log.error("[{}] Unexpected error occurred", traceId, ex);
        return ApiError.builder()
                .traceId(traceId)
                .timestamp(Instant.now().toString())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errors(Map.of("message", List.of("An unexpected error occurred")))
                .build();
    }
}
