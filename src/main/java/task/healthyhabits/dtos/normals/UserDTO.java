package task.healthyhabits.dtos.normals;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString; 
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")

public class UserDTO {
    private Long id;
    private String name;
    private String email;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private List<RoleDTO> roles;
    private List<HabitDTO> favoriteHabits;
}