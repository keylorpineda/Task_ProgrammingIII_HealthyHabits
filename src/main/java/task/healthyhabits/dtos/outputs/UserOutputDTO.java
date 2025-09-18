package task.healthyhabits.dtos.outputs;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOutputDTO {
    private Long id;
    private String name;
    private String email;
    private List<RoleOutputDTO> roles;
    private List<HabitOutputDTO> favoriteHabits;
    private Long coachId;
}
