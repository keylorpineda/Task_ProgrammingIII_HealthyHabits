package task.healthyhabits.dtosTest.inputs;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import task.healthyhabits.dtos.inputs.CompletedActivityInputDTO;
import task.healthyhabits.dtos.inputs.ProgressLogInputDTO;

import static org.assertj.core.api.Assertions.assertThat;

class ProgressLogInputDTOTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private static CompletedActivityInputDTO createCompletedActivity() {
        return new CompletedActivityInputDTO(1L, OffsetDateTime.parse("2024-01-01T10:15:30Z"), "Notes");
    }

    private static ProgressLogInputDTO createValidDto() {
        return new ProgressLogInputDTO(1L, 2L, LocalDate.of(2024, 1, 1), List.of(createCompletedActivity()));
    }

    @Test
    void shouldValidateSuccessfully() {
        assertThat(VALIDATOR.validate(createValidDto())).isEmpty();
    }

    @Test
    void shouldRequireUserId() {
        ProgressLogInputDTO dto = createValidDto();
        dto.setUserId(null);

        Set<ConstraintViolation<ProgressLogInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("userId");
            assertThat(v.getMessage()).isEqualTo("must not be null");
        });
    }

    @Test
    void shouldRequireRoutineId() {
        ProgressLogInputDTO dto = createValidDto();
        dto.setRoutineId(null);

        Set<ConstraintViolation<ProgressLogInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("routineId");
            assertThat(v.getMessage()).isEqualTo("must not be null");
        });
    }

    @Test
    void shouldRequireDate() {
        ProgressLogInputDTO dto = createValidDto();
        dto.setDate(null);

        Set<ConstraintViolation<ProgressLogInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("date");
            assertThat(v.getMessage()).isEqualTo("must not be null");
        });
    }

    @Test
    void shouldRequireCompletedActivities() {
        ProgressLogInputDTO dto = createValidDto();
        dto.setCompletedActivityInputs(null);

        Set<ConstraintViolation<ProgressLogInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("completedActivityInputs");
            assertThat(v.getMessage()).isEqualTo("must not be null");
        });
    }

    @Test
    void shouldRequireNonEmptyCompletedActivities() {
        ProgressLogInputDTO dto = createValidDto();
        dto.setCompletedActivityInputs(List.of());

        Set<ConstraintViolation<ProgressLogInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("completedActivityInputs");
            assertThat(v.getMessage()).isEqualTo("size must be between 1 and 2147483647");
        });
    }

    @Test
    void shouldCascadeCompletedActivityValidation() {
        CompletedActivityInputDTO invalidActivity = new CompletedActivityInputDTO(1L, null, "Notes");
        ProgressLogInputDTO dto = createValidDto();
        dto.setCompletedActivityInputs(List.of(invalidActivity));

        Set<ConstraintViolation<ProgressLogInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("completedActivityInputs[0].completedAt");
            assertThat(v.getMessage()).isEqualTo("must not be null");
        });
    }
}
