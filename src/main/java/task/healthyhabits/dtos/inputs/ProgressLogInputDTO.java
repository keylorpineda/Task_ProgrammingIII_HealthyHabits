package task.healthyhabits.dtos.inputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressLogInputDTO {

    @NotNull(message = "User is required.")
    private UserInputDTO user;

    @NotNull(message = "Routine is required.")
    private RoutineInputDTO routine;

    @NotNull(message = "Date is required.")
    private LocalDate date;
}