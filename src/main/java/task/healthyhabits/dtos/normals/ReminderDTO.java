package task.healthyhabits.dtos.normals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;
import task.healthyhabits.models.Frequency;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReminderDTO {
    private Long id;
    private UserDTO user;
    private HabitDTO habit;
    private LocalTime time;
    private Frequency frequency;
}