package task.healthyhabits.dtos.inputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokenInputDTO {

    @NotBlank(message = "Token is required.")
    @Size(max = 200, message = "Token cannot exceed 200 characters.")
    private String token;

    @NotNull(message = "Expiration is required.")
    @Future(message = "expiresAt must be in the future.")
    private LocalDateTime expiresAt;

    @NotNull(message = "User is required.")
    private UserInputDTO user;
}