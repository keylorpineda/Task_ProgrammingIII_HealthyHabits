package task.healthyhabits.models;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "auth_tokens")
@Data
public class AuthToken {

    @Id
    @Column(name = "id", nullable = false, length = 200)
    private String token;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
