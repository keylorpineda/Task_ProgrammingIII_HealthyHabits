package task.healthyhabits.dtos.outputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import task.healthyhabits.models.Category;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuideOutputDTO {
    private Long id;
    private String title;
    private String content;
    private Category category;
    private String objective;
    private List<HabitOutputDTO> recommendedFor;
}