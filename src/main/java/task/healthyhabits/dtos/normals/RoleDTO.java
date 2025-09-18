package task.healthyhabits.dtos.normals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import task.healthyhabits.models.Permission;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
  private Long id;
  private String name;
  private Permission permission;
}