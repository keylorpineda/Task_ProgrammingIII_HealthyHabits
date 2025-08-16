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

    @NotBlank
    @Size(max = 50)
    private String duration;

    @NotBlank
    @Size(max = 50)
    private String targetTime;
    
    @Size(max = 255)
    private String notes;
}
