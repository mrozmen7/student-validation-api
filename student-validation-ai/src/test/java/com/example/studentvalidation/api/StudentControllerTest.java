package com.example.studentvalidation.api;

import com.example.studentvalidation.dto.StudentResponse;
import com.example.studentvalidation.error.GlobalExceptionHandler;
import com.example.studentvalidation.error.StudentNotFoundException;
import com.example.studentvalidation.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller slice test — no Spring context, no DB.
 *
 * @WebMvcTest was removed in Spring Boot 4.x.
 * Standalone MockMvc with JacksonJsonHttpMessageConverter (Spring 7 / Jackson 3 native)
 * is the equivalent approach.
 */
@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Jackson 3 (tools.jackson) is the runtime in Spring Boot 4.
        // DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS=false → LocalDate serializes as "2000-01-01",
        // making JSON assertions on birthDate predictable (not asserted here, but good practice).
        JsonMapper jsonMapper = JsonMapper.builder()
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();

        mockMvc = MockMvcBuilders.standaloneSetup(studentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new JacksonJsonHttpMessageConverter(jsonMapper))
                .build();
    }

    // shared valid payload — @Past, @NotBlank, @Email, @Pattern all pass
    private static final String VALID_REQUEST_JSON = """
            {
                "firstName": "Ali",
                "lastName": "Veli",
                "birthDate": "2000-01-01",
                "email": "ali@example.com",
                "tckn": "12345678901"
            }
            """;

    // ── POST /api/students ───────────────────────────────────────────────────

    @Test
    void postStudents_shouldReturn201_whenRequestIsValid() throws Exception {
        StudentResponse response = StudentResponse.builder()
                .id(1L)
                .firstName("Ali")
                .lastName("Veli")
                .email("ali@example.com")
                .tckn("12345678901")
                .birthDate(LocalDate.of(2000, 1, 1))
                .build();

        given(studentService.create(any())).willReturn(response);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Ali"))
                .andExpect(jsonPath("$.email").value("ali@example.com"))
                .andExpect(jsonPath("$.tckn").value("12345678901"));
    }

    @Test
    void postStudents_shouldReturn400WithApiError_whenRequestHasInvalidFields() throws Exception {
        // blank firstName → @NotBlank fails; "not-an-email" → @Email fails
        String invalidRequest = """
                {
                    "firstName": "",
                    "lastName": "Veli",
                    "birthDate": "1995-01-01",
                    "email": "not-an-email",
                    "tckn": "12345678901"
                }
                """;

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.traceId").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors.firstName").exists())
                .andExpect(jsonPath("$.errors.email").exists());
    }

    // ── Email validation (POST) ──────────────────────────────────────────────

    @Test
    void postStudents_shouldReturn400WithEmailError_whenEmailIsBlank() throws Exception {
        String requestWithBlankEmail = """
                {
                    "firstName": "Ali",
                    "lastName": "Veli",
                    "birthDate": "2000-01-01",
                    "email": "",
                    "tckn": "12345678901"
                }
                """;

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestWithBlankEmail))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.traceId").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    void postStudents_shouldReturn400WithEmailError_whenEmailFormatIsInvalid() throws Exception {
        String requestWithInvalidEmail = """
                {
                    "firstName": "Ali",
                    "lastName": "Veli",
                    "birthDate": "2000-01-01",
                    "email": "not-a-valid-email",
                    "tckn": "12345678901"
                }
                """;

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestWithInvalidEmail))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.traceId").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors.email").exists());
    }

    // ── Email validation (PUT) ───────────────────────────────────────────────

    @Test
    void putStudents_shouldReturn400WithEmailError_whenEmailIsBlank() throws Exception {
        String requestWithBlankEmail = """
                {
                    "firstName": "Ali",
                    "lastName": "Veli",
                    "birthDate": "2000-01-01",
                    "email": "",
                    "tckn": "12345678901"
                }
                """;

        mockMvc.perform(put("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestWithBlankEmail))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.traceId").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    void putStudents_shouldReturn400WithEmailError_whenEmailFormatIsInvalid() throws Exception {
        String requestWithInvalidEmail = """
                {
                    "firstName": "Ali",
                    "lastName": "Veli",
                    "birthDate": "2000-01-01",
                    "email": "not-a-valid-email",
                    "tckn": "12345678901"
                }
                """;

        mockMvc.perform(put("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestWithInvalidEmail))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.traceId").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors.email").exists());
    }

    // ── GET /api/students/{id} ───────────────────────────────────────────────

    @Test
    void getStudentById_shouldReturn200WithStudentResponse_whenStudentExists() throws Exception {
        StudentResponse response = StudentResponse.builder()
                .id(1L)
                .firstName("Ali")
                .lastName("Veli")
                .email("ali@example.com")
                .tckn("12345678901")
                .birthDate(LocalDate.of(2000, 1, 1))
                .build();

        given(studentService.findById(1L)).willReturn(response);

        mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Ali"))
                .andExpect(jsonPath("$.email").value("ali@example.com"))
                .andExpect(jsonPath("$.tckn").value("12345678901"));
    }

    // ── PUT /api/students/{id} ───────────────────────────────────────────────

    @Test
    void putStudents_shouldReturn404WithApiError_whenStudentDoesNotExist() throws Exception {
        given(studentService.update(eq(99L), any())).willThrow(new StudentNotFoundException(99L));

        mockMvc.perform(put("/api/students/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.traceId").exists())
                .andExpect(jsonPath("$.errors.message[0]").value(containsString("99")));
    }
}
