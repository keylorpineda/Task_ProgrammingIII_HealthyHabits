package task.healthyhabits.dtos.inputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.util.List;
import task.healthyhabits.models.Category;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuideInputDTO {
    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    @Size(max = 500)
    private String content;

    @NotNull
    private Category category;

    @NotBlank
    @Size(max = 100)
    private String objective;
    
    @NotNull
    @Size(min = 0)
    private List<Long> recommendedHabitIds;
}
