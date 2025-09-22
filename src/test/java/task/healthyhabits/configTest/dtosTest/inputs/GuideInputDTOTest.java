package task.healthyhabits.configTest.dtosTest.inputs;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import task.healthyhabits.dtos.inputs.GuideInputDTO;
import task.healthyhabits.models.Category;

import static org.assertj.core.api.Assertions.assertThat;

class GuideInputDTOTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private static GuideInputDTO createValidDto() {
        return new GuideInputDTO("Morning", "Start strong", Category.MENTAL, "Feel better", List.of(1L, 2L));
    }

    @Test
    void shouldValidateSuccessfully() {
        assertThat(VALIDATOR.validate(createValidDto())).isEmpty();
    }

    @Test
    void shouldAllowEmptyRecommendedList() {
        GuideInputDTO dto = new GuideInputDTO("Morning", "Start strong", Category.MENTAL, "Feel better", List.of());
        assertThat(VALIDATOR.validate(dto)).isEmpty();
    }

    @Test
    void shouldRequireTitle() {
        GuideInputDTO dto = createValidDto();
        dto.setTitle(null);

        Set<ConstraintViolation<GuideInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("title");
            assertThat(v.getMessage()).isEqualTo("must not be blank");
        });
    }

    @Test
    void shouldLimitTitleLength() {
        GuideInputDTO dto = createValidDto();
        dto.setTitle("a".repeat(101));

        Set<ConstraintViolation<GuideInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("title");
            assertThat(v.getMessage()).isEqualTo("size must be between 0 and 100");
        });
    }

    @Test
    void shouldRequireContent() {
        GuideInputDTO dto = createValidDto();
        dto.setContent(null);

        Set<ConstraintViolation<GuideInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("content");
            assertThat(v.getMessage()).isEqualTo("must not be blank");
        });
    }

    @Test
    void shouldLimitContentLength() {
        GuideInputDTO dto = createValidDto();
        dto.setContent("a".repeat(501));

        Set<ConstraintViolation<GuideInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("content");
            assertThat(v.getMessage()).isEqualTo("size must be between 0 and 500");
        });
    }

    @Test
    void shouldRequireCategory() {
        GuideInputDTO dto = createValidDto();
        dto.setCategory(null);

        Set<ConstraintViolation<GuideInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("category");
            assertThat(v.getMessage()).isEqualTo("must not be null");
        });
    }

    @Test
    void shouldRequireObjective() {
        GuideInputDTO dto = createValidDto();
        dto.setObjective(null);

        Set<ConstraintViolation<GuideInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("objective");
            assertThat(v.getMessage()).isEqualTo("must not be blank");
        });
    }

    @Test
    void shouldLimitObjectiveLength() {
        GuideInputDTO dto = createValidDto();
        dto.setObjective("a".repeat(101));

        Set<ConstraintViolation<GuideInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("objective");
            assertThat(v.getMessage()).isEqualTo("size must be between 0 and 100");
        });
    }

    @Test
    void shouldRequireRecommendedIds() {
        GuideInputDTO dto = createValidDto();
        dto.setRecommendedHabitIds(null);

        Set<ConstraintViolation<GuideInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("recommendedHabitIds");
            assertThat(v.getMessage()).isEqualTo("must not be null");
        });
    }
}
