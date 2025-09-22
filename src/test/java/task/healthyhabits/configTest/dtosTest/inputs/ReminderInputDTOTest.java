package task.healthyhabits.configTest.dtosTest.inputs;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalTime;
import java.util.Set;
import org.junit.jupiter.api.Test;
import task.healthyhabits.dtos.inputs.ReminderInputDTO;
import task.healthyhabits.models.Frequency;

import static org.assertj.core.api.Assertions.assertThat;

class ReminderInputDTOTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private static ReminderInputDTO createValidDto() {
        return new ReminderInputDTO(1L, 2L, LocalTime.NOON, Frequency.DAILY);
    }

    @Test
    void shouldValidateSuccessfully() {
        assertThat(VALIDATOR.validate(createValidDto())).isEmpty();
    }

    @Test
    void shouldRequireUserId() {
        ReminderInputDTO dto = createValidDto();
        dto.setUserId(null);

        Set<ConstraintViolation<ReminderInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("userId");
            assertThat(v.getMessage()).isEqualTo("must not be null");
        });
    }

    @Test
    void shouldRequireHabitId() {
        ReminderInputDTO dto = createValidDto();
        dto.setHabitId(null);

        Set<ConstraintViolation<ReminderInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("habitId");
            assertThat(v.getMessage()).isEqualTo("must not be null");
        });
    }

    @Test
    void shouldRequireTime() {
        ReminderInputDTO dto = createValidDto();
        dto.setTime(null);

        Set<ConstraintViolation<ReminderInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("time");
            assertThat(v.getMessage()).isEqualTo("must not be null");
        });
    }

    @Test
    void shouldRequireFrequency() {
        ReminderInputDTO dto = createValidDto();
        dto.setFrequency(null);

        Set<ConstraintViolation<ReminderInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("frequency");
            assertThat(v.getMessage()).isEqualTo("must not be null");
        });
    }

}
