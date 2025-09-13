package task.healthyhabits.dtos.inputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import task.healthyhabits.models.Category;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitInputDTO {
    @NotBlank(message = "Name is required.")
    @Size(max = 100, message = "Name cannot exceed 100 characters.")
    private String name;

    @NotNull(message = "Category is required.")
    private Category category;

    @NotBlank(message = "Description is required.")
    @Size(max = 200, message = "Description cannot exceed 200 characters.")
    private String description;
}
