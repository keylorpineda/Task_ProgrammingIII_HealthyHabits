package task.healthyhabits.dtosTest.inputs;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;
import task.healthyhabits.dtos.inputs.RoutineActivityInputDTO;

import static org.assertj.core.api.Assertions.assertThat;

class RoutineActivityInputDTOTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private static RoutineActivityInputDTO createValidDto() {
        return new RoutineActivityInputDTO(1L, 30, 15, "Notes");
    }

    @Test
    void shouldValidateSuccessfully() {
        assertThat(VALIDATOR.validate(createValidDto())).isEmpty();
    }

    @Test
    void shouldRequireHabitId() {
        RoutineActivityInputDTO dto = createValidDto();
        dto.setHabitId(null);

        Set<ConstraintViolation<RoutineActivityInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("habitId");
            assertThat(v.getMessage()).isEqualTo("must not be null");
        });
    }

    @Test
    void shouldEnforceDurationMinimum() {
        RoutineActivityInputDTO dto = createValidDto();
        dto.setDuration(0);

        Set<ConstraintViolation<RoutineActivityInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("duration");
            assertThat(v.getMessage()).isEqualTo("must be greater than or equal to 1");
        });
    }

    @Test
    void shouldRequireDuration() {
        RoutineActivityInputDTO dto = createValidDto();
        dto.setDuration(null);

        Set<ConstraintViolation<RoutineActivityInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("duration");
            assertThat(v.getMessage()).isEqualTo("must not be null");
        });
    }

    @Test
    void shouldRequireTargetTime() {
        RoutineActivityInputDTO dto = createValidDto();
        dto.setTargetTime(null);

        Set<ConstraintViolation<RoutineActivityInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("targetTime");
            assertThat(v.getMessage()).isEqualTo("must not be null");
        });
    }

    @Test
    void shouldEnforceTargetTimeMinimum() {
        RoutineActivityInputDTO dto = createValidDto();
        dto.setTargetTime(-1);

        Set<ConstraintViolation<RoutineActivityInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("targetTime");
            assertThat(v.getMessage()).isEqualTo("must be greater than or equal to 0");
        });
    }

    @Test
    void shouldRestrictNotesLength() {
        RoutineActivityInputDTO dto = createValidDto();
        dto.setNotes("a".repeat(256));

        Set<ConstraintViolation<RoutineActivityInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("notes");
            assertThat(v.getMessage()).isEqualTo("size must be between 0 and 255");
        });
    }
}