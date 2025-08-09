package task.healthyhabits.dtos.outputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import task.healthyhabits.models.Category;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitOutputDTO {
    private Long id;
    private String name;
    private Category category;
    private String description;
}