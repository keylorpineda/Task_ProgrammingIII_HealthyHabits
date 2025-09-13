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
    @NotNull
    private Long userId;

    @NotNull
    private Long habitId;

    @NotNull
    private LocalTime time;
    
    @NotNull
    private Frequency frequency;
}
