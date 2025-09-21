package task.healthyhabits.dtos.outputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;
import task.healthyhabits.models.Frequency;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReminderOutputDTO {
    private Long id;
    private UserOutputDTO user;
    private HabitOutputDTO habit;
    private LocalTime time;
    private Frequency frequency;
}