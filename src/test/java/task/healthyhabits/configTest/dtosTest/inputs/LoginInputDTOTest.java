package task.healthyhabits.configTest.dtosTest.inputs;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;
import task.healthyhabits.dtos.inputs.LoginInputDTO;

import static org.assertj.core.api.Assertions.assertThat;

class LoginInputDTOTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private static LoginInputDTO createValidDto() {
        return new LoginInputDTO("alice@example.com", "password123");
    }

    @Test
    void shouldValidateSuccessfully() {
        assertThat(VALIDATOR.validate(createValidDto())).isEmpty();
    }

    @Test
    void shouldRequireEmail() {
        LoginInputDTO dto = createValidDto();
        dto.setEmail(null);

        Set<ConstraintViolation<LoginInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("email");
            assertThat(v.getMessage()).isEqualTo("Email is required.");
        });
    }

    @Test
    void shouldValidateEmailFormat() {
        LoginInputDTO dto = createValidDto();
        dto.setEmail("invalid");

        Set<ConstraintViolation<LoginInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("email");
            assertThat(v.getMessage()).isEqualTo("Email must be valid.");
        });
    }

    @Test
    void shouldLimitEmailLength() {
        LoginInputDTO dto = createValidDto();
        dto.setEmail("a".repeat(51) + "@example.com");

        Set<ConstraintViolation<LoginInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("email");
            assertThat(v.getMessage()).isEqualTo("Email cannot exceed 50 characters.");
        });
    }

    @Test
    void shouldRequirePassword() {
        LoginInputDTO dto = createValidDto();
        dto.setPassword(null);

        Set<ConstraintViolation<LoginInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("password");
            assertThat(v.getMessage()).isEqualTo("Password is required.");
        });
    }

    @Test
    void shouldEnforcePasswordLength() {
        LoginInputDTO dto = createValidDto();
        dto.setPassword("short");

        Set<ConstraintViolation<LoginInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("password");
            assertThat(v.getMessage()).isEqualTo("Password must be at least 8 characters.");
        });
    }
}

