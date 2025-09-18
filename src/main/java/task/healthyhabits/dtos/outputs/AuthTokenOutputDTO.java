package task.healthyhabits.dtos.outputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokenOutputDTO {
    private String token;
    private OffsetDateTime expiresAt; 
    private UserOutputDTO user;
}
