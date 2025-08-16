package task.healthyhabits.dtos.inputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInputDTO {

    @NotBlank(message = "Name is required.")
    @Size(max = 50, message = "Name cannot exceed 50 characters.")
    private String name;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be valid.")
    @Size(max = 50, message = "Email cannot exceed 50 characters.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters.")
    private String password;

    @NotNull(message = "Roles cannot be null.")
    private List<RoleInputDTO> roles;

    @NotNull(message = "Favorite habits cannot be null.")
    private List<HabitInputDTO> favoriteHabits;
}
