package task.healthyhabits.configTest.dtosTest.inputs;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;
import task.healthyhabits.dtos.inputs.HabitInputDTO;
import task.healthyhabits.models.Category;

import static org.assertj.core.api.Assertions.assertThat;

class HabitInputDTOTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private static HabitInputDTO createValidDto() {
        return new HabitInputDTO("Hydrate", Category.DIET, "Drink water");
    }

    @Test
    void shouldValidateSuccessfully() {
        assertThat(VALIDATOR.validate(createValidDto())).isEmpty();
    }

    @Test
    void shouldRequireName() {
        HabitInputDTO dto = createValidDto();
        dto.setName(null);

        Set<ConstraintViolation<HabitInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("name");
            assertThat(v.getMessage()).isEqualTo("Name is required.");
        });
    }

    @Test
    void shouldLimitNameLength() {
        HabitInputDTO dto = createValidDto();
        dto.setName("a".repeat(101));

        Set<ConstraintViolation<HabitInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("name");
            assertThat(v.getMessage()).isEqualTo("Name cannot exceed 100 characters.");
        });
    }

    @Test
    void shouldRequireCategory() {
        HabitInputDTO dto = createValidDto();
        dto.setCategory(null);

        Set<ConstraintViolation<HabitInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("category");
            assertThat(v.getMessage()).isEqualTo("Category is required.");
        });
    }

    @Test
    void shouldRequireDescription() {
        HabitInputDTO dto = createValidDto();
        dto.setDescription(null);

        Set<ConstraintViolation<HabitInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("description");
            assertThat(v.getMessage()).isEqualTo("Description is required.");
        });
    }

    @Test
    void shouldLimitDescriptionLength() {
        HabitInputDTO dto = createValidDto();
        dto.setDescription("a".repeat(201));

        Set<ConstraintViolation<HabitInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("description");
            assertThat(v.getMessage()).isEqualTo("Description cannot exceed 200 characters.");
        });
    }
}