package task.healthyhabits.dtos.inputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import task.healthyhabits.models.Permission;
import jakarta.validation.constraints.*;

@Data 
@NoArgsConstructor
@AllArgsConstructor
public class RoleInputDTO {
    @NotBlank(message = "Role name is required.")
    @Size(max = 50, message = "Role name cannot exceed 50 characters.")
    private String name;

    @NotNull(message = "Permission is required.")
    private Permission permission;
}