package task.healthyhabits.dtosTest.inputs;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import task.healthyhabits.dtos.inputs.RoutineActivityInputDTO;
import task.healthyhabits.dtos.inputs.RoutineInputDTO;
import task.healthyhabits.models.DaysOfWeek;

import static org.assertj.core.api.Assertions.assertThat;

class RoutineInputDTOTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private static RoutineActivityInputDTO createActivity() {
        return new RoutineActivityInputDTO(1L, 30, 15, "Notes");
    }

    private static RoutineInputDTO createValidDto() {
        return new RoutineInputDTO(
                "Morning",
                "Start strong",
                List.of("energy"),
                List.of(DaysOfWeek.MONDAY),
                2L,
                List.of(createActivity()));
    }

    @Test
    void shouldValidateSuccessfully() {
        assertThat(VALIDATOR.validate(createValidDto())).isEmpty();
    }

    @Test
    void shouldRequireTitle() {
        RoutineInputDTO dto = createValidDto();
        dto.setTitle(" ");

        Set<ConstraintViolation<RoutineInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("title");
            assertThat(v.getMessage()).isEqualTo("Name is required.");
        });
    }

    @Test
    void shouldLimitTitleLength() {
        RoutineInputDTO dto = createValidDto();
        dto.setTitle("a".repeat(101));

        Set<ConstraintViolation<RoutineInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("title");
            assertThat(v.getMessage()).isEqualTo("size must be between 0 and 100");
        });
    }

    @Test
    void shouldRequireDescription() {
        RoutineInputDTO dto = createValidDto();
        dto.setDescription(" ");

        Set<ConstraintViolation<RoutineInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("description");
            assertThat(v.getMessage()).isEqualTo("must not be blank");
        });
    }

    @Test
    void shouldLimitDescriptionLength() {
        RoutineInputDTO dto = createValidDto();
        dto.setDescription("a".repeat(201));

        Set<ConstraintViolation<RoutineInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("description");
            assertThat(v.getMessage()).isEqualTo("size must be between 0 and 200");
        });
    }

    @Test
    void shouldRequireDaysOfWeek() {
        RoutineInputDTO dto = createValidDto();
        dto.setDaysOfWeek(null);

        Set<ConstraintViolation<RoutineInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("daysOfWeek");
            assertThat(v.getMessage()).isEqualTo("must not be null");
        });
    }

    @Test
    void shouldRequireAtLeastOneDay() {
        RoutineInputDTO dto = createValidDto();
        dto.setDaysOfWeek(List.of());

        Set<ConstraintViolation<RoutineInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("daysOfWeek");
            assertThat(v.getMessage()).isEqualTo("size must be between 1 and 2147483647");
        });
    }

    @Test
    void shouldRequireUserId() {
        RoutineInputDTO dto = createValidDto();
        dto.setUserId(null);

        Set<ConstraintViolation<RoutineInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("userId");
            assertThat(v.getMessage()).isEqualTo("must not be null");
        });
    }

    @Test
    void shouldRequireActivities() {
        RoutineInputDTO dto = createValidDto();
        dto.setActivityInputs(null);

        Set<ConstraintViolation<RoutineInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("activityInputs");
            assertThat(v.getMessage()).isEqualTo("must not be null");
        });
    }

    @Test
    void shouldRequireNonEmptyActivities() {
        RoutineInputDTO dto = createValidDto();
        dto.setActivityInputs(List.of());

        Set<ConstraintViolation<RoutineInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("activityInputs");
            assertThat(v.getMessage()).isEqualTo("size must be between 1 and 2147483647");
        });
    }

    @Test
    void shouldCascadeActivityValidation() {
        RoutineActivityInputDTO invalidActivity = new RoutineActivityInputDTO(1L, 0, 10, "Notes");
        RoutineInputDTO dto = createValidDto();
        dto.setActivityInputs(List.of(invalidActivity));

        Set<ConstraintViolation<RoutineInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("activityInputs[0].duration");
            assertThat(v.getMessage()).isEqualTo("must be greater than or equal to 1");
        });
    }
}