package task.healthyhabits.dtos.inputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompletedActivityInputDTO {

    @NotNull(message = "Habit is required.")
    private HabitInputDTO habit;

    @NotNull(message = "completedAt is required.")
    @PastOrPresent(message = "completedAt must be in the past or present.")
    private LocalDateTime completedAt;

    @Size(max = 200, message = "Notes cannot exceed 200 characters.")
    private String notes;

    @NotNull(message = "Progress log is required.")
    private ProgressLogInputDTO progressLog;
}