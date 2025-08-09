package task.healthyhabits.dtos.inputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleInputDTO {

    @NotBlank(message = "Role name is required.")
    @Size(max = 50, message = "Role name cannot exceed 50 characters.")
    private String name;

    @NotBlank(message = "Permissions are required.")
    @Size(max = 200, message = "Permissions cannot exceed 200 characters.")
    private String permissions;
}