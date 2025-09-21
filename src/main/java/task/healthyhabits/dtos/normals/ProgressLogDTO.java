package task.healthyhabits.dtos.normals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressLogDTO {
    private Long id;
    private UserDTO user;
    private RoutineDTO routine;
    private LocalDate date;
    private List<CompletedActivityDTO> completedActivities;
}