package task.healthyhabits.dtos.inputs;

import java.time.OffsetDateTime;

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
    private OffsetDateTime completedAt;

    @Size(max = 255)
    private String notes;
}