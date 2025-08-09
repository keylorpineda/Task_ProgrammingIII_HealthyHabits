package task.healthyhabits.dtos.outputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompletedActivityOutputDTO {
    private Long id;
    private HabitOutputDTO habit;
    private LocalDateTime completedAt;
    private String notes;
    private ProgressLogOutputDTO progressLog;
}