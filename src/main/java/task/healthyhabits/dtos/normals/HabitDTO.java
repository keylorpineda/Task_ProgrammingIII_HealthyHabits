package task.healthyhabits.dtos.normals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import task.healthyhabits.models.Category;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitDTO {
    private Long id;
    private String name;
    private Category category;
    private String description;
}