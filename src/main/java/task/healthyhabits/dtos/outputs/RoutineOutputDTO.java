package task.healthyhabits.dtos.outputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import task.healthyhabits.models.DaysOfWeek;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutineOutputDTO {
    private Long id;
    private String title;
    private UserOutputDTO user;
    private String description;
    private List<String> tags;
    private List<DaysOfWeek> daysOfWeek;
    private List<RoutineActivityOutputDTO> activities;
}