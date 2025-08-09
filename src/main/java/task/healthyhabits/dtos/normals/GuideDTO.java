package task.healthyhabits.dtos.normals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import task.healthyhabits.models.Category;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuideDTO {
    private Long id;
    private String title;
    private String content;
    private Category category;
    private List<HabitDTO> recommendedFor;
}
