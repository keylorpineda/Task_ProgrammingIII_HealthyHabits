package task.healthyhabits.services.reminder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import task.healthyhabits.dtos.inputs.ReminderInputDTO;
import task.healthyhabits.dtos.normals.ReminderDTO;
import task.healthyhabits.dtos.outputs.ReminderOutputDTO;

public interface ReminderService {
    Page<ReminderDTO> list(Pageable pageable);
    Page<ReminderDTO> myReminders(Long userId, Pageable pageable);
    ReminderDTO findByIdOrNull(Long id);
    ReminderOutputDTO create(ReminderInputDTO input);
    ReminderOutputDTO update(Long id, ReminderInputDTO input);
    boolean delete(Long id);
}
