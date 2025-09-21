package task.healthyhabits.dtos.normals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import task.healthyhabits.models.DaysOfWeek;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutineDTO {
    private Long id;
    private String title;
    private UserDTO user;
    private String description;
    private List<String> tags;
    private List<DaysOfWeek> daysOfWeek;
    private List<RoutineActivityDTO> activities;
}