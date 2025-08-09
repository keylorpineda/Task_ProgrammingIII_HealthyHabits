package task.healthyhabits.dtos.inputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import task.healthyhabits.models.DaysOfWeek;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutineInputDTO {

    @NotBlank(message = "Title is required.")
    @Size(max = 100, message = "Title cannot exceed 100 characters.")
    private String title;

    @NotNull(message = "User is required.")
    private UserInputDTO user;

    @NotBlank(message = "Description is required.")
    @Size(max = 200, message = "Description cannot exceed 200 characters.")
    private String description;

    @NotEmpty(message = "At least one day of week is required.")
    private List<DaysOfWeek> daysOfWeek;

    @NotNull(message = "Activities are required.")
    private List<RoutineActivityInputDTO> activities;
}