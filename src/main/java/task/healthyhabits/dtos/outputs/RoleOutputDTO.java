package task.healthyhabits.dtos.outputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import task.healthyhabits.models.Permission;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleOutputDTO {
    private Long id;
    private String name;
    private Permission permission;
}