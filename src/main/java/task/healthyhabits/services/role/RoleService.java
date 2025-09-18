package task.healthyhabits.services.role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import task.healthyhabits.dtos.inputs.RoleInputDTO;
import task.healthyhabits.dtos.normals.RoleDTO;
import task.healthyhabits.dtos.outputs.RoleOutputDTO;

public interface RoleService {
    Page<RoleDTO> list(Pageable pageable);
    RoleDTO findByIdOrNull(Long id);
    RoleOutputDTO create(RoleInputDTO input);
    RoleOutputDTO update(Long id, RoleInputDTO input);
    boolean delete(Long id);
}
