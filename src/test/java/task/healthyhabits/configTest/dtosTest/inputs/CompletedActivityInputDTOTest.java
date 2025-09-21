package task.healthyhabits.dtosTest.inputs;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.OffsetDateTime;
import java.util.Set;
import org.junit.jupiter.api.Test;
import task.healthyhabits.dtos.inputs.CompletedActivityInputDTO;

import static org.assertj.core.api.Assertions.assertThat;

class CompletedActivityInputDTOTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private static CompletedActivityInputDTO createValidDto() {
        return new CompletedActivityInputDTO(1L, OffsetDateTime.parse("2024-01-01T10:15:30Z"), "Notes");
    }

    @Test
    void shouldValidateSuccessfully() {
        assertThat(VALIDATOR.validate(createValidDto())).isEmpty();
    }

    @Test
    void shouldRequireHabitId() {
        CompletedActivityInputDTO dto = createValidDto();
        dto.setHabitId(null);

        Set<ConstraintViolation<CompletedActivityInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("habitId");
            assertThat(v.getMessage()).isEqualTo("must not be null");
        });
    }

    @Test
    void shouldRequireCompletedAt() {
        CompletedActivityInputDTO dto = createValidDto();
        dto.setCompletedAt(null);

        Set<ConstraintViolation<CompletedActivityInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("completedAt");
            assertThat(v.getMessage()).isEqualTo("must not be null");
        });
    }

    @Test
    void shouldRestrictNotesLength() {
        CompletedActivityInputDTO dto = createValidDto();
        dto.setNotes("a".repeat(256));

        Set<ConstraintViolation<CompletedActivityInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("notes");
            assertThat(v.getMessage()).isEqualTo("size must be between 0 and 255");
        });
    }
}

