package task.healthyhabits.configTest.dtosTest.inputs;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;
import task.healthyhabits.dtos.inputs.RoleInputDTO;
import task.healthyhabits.models.Permission;

import static org.assertj.core.api.Assertions.assertThat;

class RoleInputDTOTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private static RoleInputDTO createValidDto() {
        return new RoleInputDTO("Coach", Permission.ROUTINE_EDITOR);
    }

    @Test
    void shouldValidateSuccessfully() {
        assertThat(VALIDATOR.validate(createValidDto())).isEmpty();
    }

    @Test
    void shouldRequireName() {
        RoleInputDTO dto = createValidDto();
        dto.setName(null);

        Set<ConstraintViolation<RoleInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("name");
            assertThat(v.getMessage()).isEqualTo("Role name is required.");
        });
    }

    @Test
    void shouldLimitNameLength() {
        RoleInputDTO dto = createValidDto();
        dto.setName("a".repeat(51));

        Set<ConstraintViolation<RoleInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("name");
            assertThat(v.getMessage()).isEqualTo("Role name cannot exceed 50 characters.");
        });
    }

    @Test
    void shouldRequirePermission() {
        RoleInputDTO dto = createValidDto();
        dto.setPermission(null);

        Set<ConstraintViolation<RoleInputDTO>> violations = VALIDATOR.validate(dto);
        assertThat(violations).singleElement().satisfies(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("permission");
            assertThat(v.getMessage()).isEqualTo("Permission is required.");
        });
    }
}
