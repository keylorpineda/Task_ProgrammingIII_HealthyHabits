package task.healthyhabits.services.progressLog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import task.healthyhabits.dtos.inputs.ProgressLogInputDTO;
import task.healthyhabits.dtos.normals.ProgressLogDTO;
import task.healthyhabits.dtos.outputs.ProgressLogOutputDTO;

import java.time.LocalDate;

public interface ProgressLogService {
    Page<ProgressLogDTO> list(Pageable pageable);
    ProgressLogDTO findByIdOrNull(Long id);
    ProgressLogDTO byDate(Long userId, LocalDate date);
    Page<ProgressLogDTO> byRange(Long userId, LocalDate from, LocalDate to, Pageable pageable);
    ProgressLogOutputDTO create(ProgressLogInputDTO input);
    ProgressLogOutputDTO update(Long id, ProgressLogInputDTO input);
    boolean delete(Long id);
}
