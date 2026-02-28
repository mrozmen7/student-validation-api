package com.example.studentvalidation.service;

import com.example.studentvalidation.domain.Student;
import com.example.studentvalidation.dto.PagedStudentResponse;
import com.example.studentvalidation.dto.StudentRequest;
import com.example.studentvalidation.dto.StudentResponse;
import com.example.studentvalidation.error.BusinessRuleException;
import com.example.studentvalidation.error.StudentNotFoundException;
import com.example.studentvalidation.mapper.StudentMapper;
import com.example.studentvalidation.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentServiceImpl studentService;

    // ── findAll ─────────────────────────────────────────────────────────────

    @Test
    void findAll_shouldReturnPagedResponse_whenPageableProvided() {
        Pageable pageable = PageRequest.of(0, 10);
        Student student = new Student();
        StudentResponse studentResponse = StudentResponse.builder()
                .id(1L).firstName("Ali").lastName("Veli")
                .email("ali@example.com").tckn("12345678901")
                .birthDate(LocalDate.of(2000, 1, 1)).build();

        Page<Student> page = new PageImpl<>(List.of(student), pageable, 1);
        given(studentRepository.findAll(pageable)).willReturn(page);
        given(studentMapper.toResponse(student)).willReturn(studentResponse);

        PagedStudentResponse result = studentService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getPage()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isTrue();
    }

    @Test
    void findAll_shouldReturnEmptyPage_whenNoStudentsExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Student> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        given(studentRepository.findAll(pageable)).willReturn(emptyPage);

        PagedStudentResponse result = studentService.findAll(pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(0);
    }

    @Test
    void findAll_shouldPassPageableToRepository() {
        Pageable pageable = PageRequest.of(1, 5);
        Page<Student> page = new PageImpl<>(List.of(), pageable, 0);
        given(studentRepository.findAll(pageable)).willReturn(page);

        studentService.findAll(pageable);

        verify(studentRepository).findAll(pageable);
    }

    // ── create ──────────────────────────────────────────────────────────────

    @Test
    void create_shouldReturnStudentResponse_whenRequestIsValid() {
        StudentRequest request = buildRequest(LocalDate.now().minusYears(25));
        Student entity = new Student();
        StudentResponse expected = StudentResponse.builder()
                .id(1L)
                .firstName("Ali")
                .lastName("Veli")
                .email("ali@example.com")
                .tckn("12345678901")
                .birthDate(request.getBirthDate())
                .build();

        given(studentRepository.existsByEmail(request.getEmail())).willReturn(false);
        given(studentRepository.existsByTckn(request.getTckn())).willReturn(false);
        given(studentMapper.toEntity(request)).willReturn(entity);
        given(studentRepository.save(entity)).willReturn(entity);
        given(studentMapper.toResponse(entity)).willReturn(expected);

        StudentResponse actual = studentService.create(request);

        assertThat(actual).isEqualTo(expected);
        verify(studentRepository).save(entity);
    }

    @Test
    void create_shouldThrowBusinessRuleException_whenEmailAlreadyExists() {
        StudentRequest request = buildRequest(LocalDate.now().minusYears(25));
        given(studentRepository.existsByEmail(request.getEmail())).willReturn(true);

        assertThatThrownBy(() -> studentService.create(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining(request.getEmail());
    }

    @Test
    void create_shouldThrowBusinessRuleException_whenTcknAlreadyExists() {
        StudentRequest request = buildRequest(LocalDate.now().minusYears(25));
        given(studentRepository.existsByEmail(request.getEmail())).willReturn(false);
        given(studentRepository.existsByTckn(request.getTckn())).willReturn(true);

        assertThatThrownBy(() -> studentService.create(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining(request.getTckn());
    }

    @Test
    void create_shouldThrowBusinessRuleException_whenAgeIsUnder18() {
        StudentRequest request = buildRequest(LocalDate.now().minusYears(10));

        assertThatThrownBy(() -> studentService.create(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("18");
    }

    @Test
    void create_shouldThrowBusinessRuleException_whenAgeIsOver65() {
        StudentRequest request = buildRequest(LocalDate.now().minusYears(70));

        assertThatThrownBy(() -> studentService.create(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("65");
    }

    // ── update ──────────────────────────────────────────────────────────────

    @Test
    void update_shouldThrowStudentNotFoundException_whenStudentDoesNotExist() {
        Long nonExistentId = 99L;
        StudentRequest request = buildRequest(LocalDate.now().minusYears(25));
        given(studentRepository.findById(nonExistentId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.update(nonExistentId, request))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining(String.valueOf(nonExistentId));
    }

    @Test
    void update_shouldReturnUpdatedStudentResponse_whenRequestIsValid() {
        Long id = 1L;
        StudentRequest request = buildRequest(LocalDate.now().minusYears(25));
        Student existing = new Student();
        StudentResponse expected = StudentResponse.builder()
                .id(id)
                .firstName("Ali")
                .lastName("Veli")
                .email("ali@example.com")
                .tckn("12345678901")
                .birthDate(request.getBirthDate())
                .build();

        given(studentRepository.findById(id)).willReturn(Optional.of(existing));
        given(studentRepository.existsByEmailAndIdNot(request.getEmail(), id)).willReturn(false);
        given(studentRepository.existsByTcknAndIdNot(request.getTckn(), id)).willReturn(false);
        given(studentRepository.save(existing)).willReturn(existing);
        given(studentMapper.toResponse(existing)).willReturn(expected);

        StudentResponse actual = studentService.update(id, request);

        assertThat(actual).isEqualTo(expected);
        verify(studentMapper).updateEntity(request, existing);
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private StudentRequest buildRequest(LocalDate birthDate) {
        return new StudentRequest("Ali", "Veli", birthDate, "ali@example.com", "12345678901");
    }
}
