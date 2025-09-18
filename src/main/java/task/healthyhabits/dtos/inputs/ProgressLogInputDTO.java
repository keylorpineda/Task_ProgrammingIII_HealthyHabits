package task.healthyhabits.dtos.inputs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressLogInputDTO {
    @NotNull
    private Long userId;

    @NotNull
    private Long routineId;

    @NotNull
    private LocalDate date;

    @NotNull @Size(min = 1) @Valid
    private List<CompletedActivityInputDTO> completedActivityInputs;
}