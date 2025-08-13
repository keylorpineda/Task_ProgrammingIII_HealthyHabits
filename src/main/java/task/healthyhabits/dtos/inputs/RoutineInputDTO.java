package task.healthyhabits.dtos.inputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import task.healthyhabits.models.DaysOfWeek;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutineInputDTO {

    @NotBlank(message = "Name is required.") @Size(max = 100)
    private String title;

    @NotBlank @Size(max = 200)
    private String description;

    @NotNull @Size(min = 1)
    private List<DaysOfWeek> daysOfWeek;

    @NotNull
    private Long userId;

    @NotNull @Size(min = 1) @Valid
    private List<RoutineActivityInputDTO> activityInputs;
}
