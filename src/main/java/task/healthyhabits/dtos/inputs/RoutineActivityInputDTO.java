package task.healthyhabits.dtos.inputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutineActivityInputDTO {
    @NotNull
    private Long habitId;

    @NotNull @Min(1)
    private Integer duration;

    @NotNull @Min(0)
    private Integer targetTime;

    @Size(max = 255)
    private String notes;
}
