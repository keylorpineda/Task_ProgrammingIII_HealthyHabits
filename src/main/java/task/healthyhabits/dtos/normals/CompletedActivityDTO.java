package task.healthyhabits.dtos.normals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompletedActivityDTO {
    private Long id;
    private HabitDTO habit;
    private OffsetDateTime completedAt;
    private String notes;
    private ProgressLogDTO progressLog;
}
