package task.healthyhabits.dtosTest.inputs;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import task.healthyhabits.dtos.inputs.HabitInputDTO;
import task.healthyhabits.dtos.inputs.RoleInputDTO;
import task.healthyhabits.dtos.inputs.UserInputDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.Permission;

import static org.assertj.core.api.Assertions.assertThat;

class UserInputDTOTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private static UserInputDTO createValidDto() {
        RoleInputDTO role = new RoleInputDTO("Coach", Permission.ROUTINE_EDITOR);
        HabitInputDTO habit = new HabitInputDTO("Hydrate", Category.DIET, "Drink water");
        return new UserInputDTO("Alice", "alice@example.com", "password123", List.of(role), List.of(habit), 2L);
    }

    @Test
    void shouldValidateSuccessfully() {
        assertThat(VALIDATOR.validate(createValidDto())).isEmpty();
    }

    @Test
    void shouldRequireName() {
        UserInputDTO dto = createValidDto();
        dto.setName(" ");

        Set<ConstraintViolation<UserInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("name");
            assertThat(v.getMessage()).isEqualTo("Name is required.");
        });
    }

    @Test
    void shouldLimitNameLength() {
        UserInputDTO dto = createValidDto();
        dto.setName("a".repeat(51));

        Set<ConstraintViolation<UserInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("name");
            assertThat(v.getMessage()).isEqualTo("Name cannot exceed 50 characters.");
        });
    }

    @Test
    void shouldRequireEmail() {
        UserInputDTO dto = createValidDto();
        dto.setEmail(" ");

        Set<ConstraintViolation<UserInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("email");
            assertThat(v.getMessage()).isEqualTo("Email is required.");
        });
    }

    @Test
    void shouldValidateEmailFormat() {
        UserInputDTO dto = createValidDto();
        dto.setEmail("invalid");

        Set<ConstraintViolation<UserInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("email");
            assertThat(v.getMessage()).isEqualTo("Email must be valid.");
        });
    }

    @Test
    void shouldLimitEmailLength() {
        UserInputDTO dto = createValidDto();
        dto.setEmail("a".repeat(51) + "@example.com");

        Set<ConstraintViolation<UserInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("email");
            assertThat(v.getMessage()).isEqualTo("Email cannot exceed 50 characters.");
        });
    }

    @Test
    void shouldRequirePassword() {
        UserInputDTO dto = createValidDto();
        dto.setPassword(" ");

        Set<ConstraintViolation<UserInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("password");
            assertThat(v.getMessage()).isEqualTo("Password is required.");
        });
    }

    @Test
    void shouldEnforcePasswordLength() {
        UserInputDTO dto = createValidDto();
        dto.setPassword("short");

        Set<ConstraintViolation<UserInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("password");
            assertThat(v.getMessage()).isEqualTo("Password must be at least 8 characters.");
        });
    }

    @Test
    void shouldRequireRoles() {
        UserInputDTO dto = createValidDto();
        dto.setRoles(null);

        Set<ConstraintViolation<UserInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("roles");
            assertThat(v.getMessage()).isEqualTo("Roles cannot be null.");
        });
    }

    @Test
    void shouldRequireFavoriteHabits() {
        UserInputDTO dto = createValidDto();
        dto.setFavoriteHabits(null);

        Set<ConstraintViolation<UserInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("favoriteHabits");
            assertThat(v.getMessage()).isEqualTo("Favorite habits cannot be null.");
        });
    }

    @Test
    void shouldRequirePositiveCoachId() {
        UserInputDTO dto = createValidDto();
        dto.setCoachId(0L);

        Set<ConstraintViolation<UserInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("coachId");
            assertThat(v.getMessage()).isEqualTo("Coach id must be positive.");
        });
    }
}