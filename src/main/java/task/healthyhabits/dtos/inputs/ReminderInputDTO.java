package task.healthyhabits.dtos.inputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import task.healthyhabits.models.Frequency;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReminderInputDTO {

    @NotNull(message = "User is required.")
    private UserInputDTO user;

    @NotNull(message = "Habit is required.")
    private HabitInputDTO habit;

    @NotNull(message = "Time is required.")
    private LocalTime time;

    @NotNull(message = "Frequency is required.")
    private Frequency frequency;
}
