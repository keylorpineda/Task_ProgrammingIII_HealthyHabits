package task.healthyhabits.dtos.normals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompletedActivityDTO {
    private Long id;
    private HabitDTO habit;
    private LocalDateTime completedAt;
    private String notes;
    private ProgressLogDTO progressLog;
}
