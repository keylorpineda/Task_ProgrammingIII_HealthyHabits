package task.healthyhabits.services.routineActivity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import task.healthyhabits.dtos.inputs.RoutineActivityInputDTO;
import task.healthyhabits.dtos.normals.RoutineActivityDTO;
import task.healthyhabits.dtos.outputs.RoutineActivityOutputDTO;

public interface RoutineActivityService {
    Page<RoutineActivityDTO> list(Pageable pageable);
    RoutineActivityDTO findByIdOrNull(Long id);
    RoutineActivityOutputDTO create(Long routineId, RoutineActivityInputDTO input);
    RoutineActivityOutputDTO update(Long id, RoutineActivityInputDTO input);
    boolean delete(Long id);
}
