package task.healthyhabits.dtos.outputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutineActivityOutputDTO {
    private Long id;
    private HabitOutputDTO habit;
    private String duration;
    private String targetTime;
    private String notes;
    private RoutineOutputDTO routine;
}