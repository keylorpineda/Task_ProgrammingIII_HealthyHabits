package task.healthyhabits.configTest.dtosTest.inputs;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import task.healthyhabits.dtos.inputs.AuthTokenInputDTO;
import task.healthyhabits.dtos.inputs.HabitInputDTO;
import task.healthyhabits.dtos.inputs.RoleInputDTO;
import task.healthyhabits.dtos.inputs.UserInputDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.Permission;

import static org.assertj.core.api.Assertions.assertThat;

class AuthTokenInputDTOTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private static UserInputDTO createUser() {
        RoleInputDTO role = new RoleInputDTO("Coach", Permission.ROUTINE_EDITOR);
        HabitInputDTO habit = new HabitInputDTO("Hydrate", Category.DIET, "Drink water");
        return new UserInputDTO("Alice", "alice@example.com", "password123", List.of(role), List.of(habit), 1L);
    }

    private static AuthTokenInputDTO createValidDto() {
        return new AuthTokenInputDTO("token", LocalDateTime.now().plusDays(1), createUser());
    }

    @Test
    void shouldValidateSuccessfully() {
        assertThat(VALIDATOR.validate(createValidDto())).isEmpty();
    }

    @Test
    void shouldRequireToken() {
        AuthTokenInputDTO dto = createValidDto();
        dto.setToken(null);

        Set<ConstraintViolation<AuthTokenInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("token");
            assertThat(v.getMessage()).isEqualTo("Token is required.");
        });
    }

    @Test
    void shouldLimitTokenLength() {
        AuthTokenInputDTO dto = createValidDto();
        dto.setToken("a".repeat(201));

        Set<ConstraintViolation<AuthTokenInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("token");
            assertThat(v.getMessage()).isEqualTo("Token cannot exceed 200 characters.");
        });
    }

    @Test
    void shouldRequireExpiration() {
        AuthTokenInputDTO dto = createValidDto();
        dto.setExpiresAt(null);

        Set<ConstraintViolation<AuthTokenInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("expiresAt");
            assertThat(v.getMessage()).isEqualTo("Expiration is required.");
        });
    }

    @Test
    void shouldRequireFutureExpiration() {
        AuthTokenInputDTO dto = createValidDto();
        dto.setExpiresAt(LocalDateTime.now().minusDays(1));

        Set<ConstraintViolation<AuthTokenInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("expiresAt");
            assertThat(v.getMessage()).isEqualTo("expiresAt must be in the future.");
        });
    }

    @Test
    void shouldRequireUser() {
        AuthTokenInputDTO dto = createValidDto();
        dto.setUser(null);

        Set<ConstraintViolation<AuthTokenInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("user");
            assertThat(v.getMessage()).isEqualTo("User is required.");
        });
    }
}
