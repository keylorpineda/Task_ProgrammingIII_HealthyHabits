package task.healthyhabits.dtos.normals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutineActivityDTO {
    private Long id;
    private HabitDTO habit;
    private String duration;
    private String targetTime;
    private String notes;
    private RoutineDTO routine;
}