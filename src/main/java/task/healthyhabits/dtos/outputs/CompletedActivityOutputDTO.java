package task.healthyhabits.dtos.outputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompletedActivityOutputDTO {
    private Long id;
    private HabitOutputDTO habit;
    private OffsetDateTime completedAt;
    private String notes;
    private ProgressLogOutputDTO progressLog;
}