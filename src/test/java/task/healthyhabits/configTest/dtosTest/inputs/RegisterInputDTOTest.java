package task.healthyhabits.configTest.dtosTest.inputs;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;
import task.healthyhabits.dtos.inputs.RegisterInputDTO;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterInputDTOTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private static RegisterInputDTO createValidDto() {
        return new RegisterInputDTO("Alice", "alice@example.com", "password123");
    }

    @Test
    void shouldValidateSuccessfully() {
        assertThat(VALIDATOR.validate(createValidDto())).isEmpty();
    }

    @Test
    void shouldRequireName() {
        RegisterInputDTO dto = createValidDto();
        dto.setName(null);

        Set<ConstraintViolation<RegisterInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("name");
            assertThat(v.getMessage()).isEqualTo("Name is required.");
        });
    }

    @Test
    void shouldLimitNameLength() {
        RegisterInputDTO dto = createValidDto();
        dto.setName("a".repeat(51));

        Set<ConstraintViolation<RegisterInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("name");
            assertThat(v.getMessage()).isEqualTo("Name cannot exceed 50 characters.");
        });
    }

    @Test
    void shouldRequireEmail() {
        RegisterInputDTO dto = createValidDto();
        dto.setEmail(null);

        Set<ConstraintViolation<RegisterInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("email");
            assertThat(v.getMessage()).isEqualTo("Email is required.");
        });
    }

    @Test
    void shouldValidateEmailFormat() {
        RegisterInputDTO dto = createValidDto();
        dto.setEmail("invalid");

        Set<ConstraintViolation<RegisterInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("email");
            assertThat(v.getMessage()).isEqualTo("Email must be valid.");
        });
    }

    @Test
    void shouldLimitEmailLength() {
        RegisterInputDTO dto = createValidDto();
        dto.setEmail("a".repeat(51) + "@example.com");

        Set<ConstraintViolation<RegisterInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("email");
            assertThat(v.getMessage()).isEqualTo("Email cannot exceed 50 characters.");
        });
    }

    @Test
    void shouldRequirePassword() {
        RegisterInputDTO dto = createValidDto();
        dto.setPassword(null);

        Set<ConstraintViolation<RegisterInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("password");
            assertThat(v.getMessage()).isEqualTo("Password is required.");
        });
    }

    @Test
    void shouldEnforcePasswordLength() {
        RegisterInputDTO dto = createValidDto();
        dto.setPassword("short");

        Set<ConstraintViolation<RegisterInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("password");
            assertThat(v.getMessage()).isEqualTo("Password must be at least 8 characters.");
        });
    }
}
