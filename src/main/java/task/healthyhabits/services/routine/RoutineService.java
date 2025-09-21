package task.healthyhabits.services.routine;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import task.healthyhabits.dtos.inputs.RoutineInputDTO;
import task.healthyhabits.dtos.normals.RoutineDTO;
import task.healthyhabits.dtos.outputs.RoutineOutputDTO;

public interface RoutineService {
    Page<RoutineDTO> list(Pageable pageable);
    Page<RoutineDTO> myRoutines(Long userId, Pageable pageable);
    Page<RoutineDTO> byUser(Long userId, Pageable pageable);
    RoutineDTO findByIdOrNull(Long id);
    RoutineOutputDTO create(RoutineInputDTO input);
    RoutineOutputDTO update(Long id, RoutineInputDTO input);
    boolean delete(Long id);
}
