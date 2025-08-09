package task.healthyhabits.dtos.outputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokenOutputDTO {
    private String token;
    private LocalDateTime expiresAt;
    private UserOutputDTO user;
}
