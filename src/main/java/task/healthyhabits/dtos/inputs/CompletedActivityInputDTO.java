package task.healthyhabits.dtos.inputs;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompletedActivityInputDTO {

    @NotNull
    private Long habitId;

    @NotNull
    private LocalDateTime completedAt;

    @Size(max = 255)
    private String notes;
}