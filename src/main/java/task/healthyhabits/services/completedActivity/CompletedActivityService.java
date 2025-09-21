package task.healthyhabits.services.completedActivity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import task.healthyhabits.dtos.inputs.CompletedActivityInputDTO;
import task.healthyhabits.dtos.normals.CompletedActivityDTO;
import task.healthyhabits.dtos.outputs.CompletedActivityOutputDTO;

public interface CompletedActivityService {
    Page<CompletedActivityDTO> list(Pageable pageable);
    CompletedActivityDTO findByIdOrNull(Long id);
    CompletedActivityOutputDTO create(Long progressLogId, CompletedActivityInputDTO input);
    CompletedActivityOutputDTO update(Long id, CompletedActivityInputDTO input);
    boolean delete(Long id);
}
