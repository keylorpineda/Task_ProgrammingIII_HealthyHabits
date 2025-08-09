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

    @NotBlank(message = "Title is required.")
    @Size(max = 100, message = "Title cannot exceed 100 characters.")
    private String title;

    @NotBlank(message = "Content is required.")
    @Size(max = 500, message = "Content cannot exceed 500 characters.")
    private String content;

    @NotNull(message = "Category is required.")
    private Category category;

    @NotNull(message = "Recommended habits list cannot be null.")
    private List<HabitInputDTO> recommendedFor;
}
