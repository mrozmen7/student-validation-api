package com.example.studentvalidation.error;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class ApiError {

    private String traceId;
    private String timestamp;
    private int status;
    private Map<String, List<String>> errors;
}
