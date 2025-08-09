package task.healthyhabits.dtos.inputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutineActivityInputDTO {

    @NotNull(message = "Habit is required.")
    private HabitInputDTO habit;

    @NotBlank(message = "Duration is required.")
    @Size(max = 50, message = "Duration cannot exceed 50 characters.")
    private String duration;

    @NotBlank(message = "Target time is required.")
    @Size(max = 50, message = "Target time cannot exceed 50 characters.")
    private String targetTime;

    @Size(max = 255, message = "Notes cannot exceed 255 characters.")
    private String notes;

    @NotNull(message = "Routine is required.")
    private RoutineInputDTO routine;
}