package task.healthyhabits.services.habit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import task.healthyhabits.dtos.inputs.HabitInputDTO;
import task.healthyhabits.dtos.normals.HabitDTO;
import task.healthyhabits.dtos.outputs.HabitOutputDTO;
import task.healthyhabits.models.Category;

public interface HabitService {
    Page<HabitDTO> list(Pageable pageable);
    Page<HabitDTO> byCategory(Category category, Pageable pageable);
    HabitDTO findByIdOrNull(Long id);
    HabitOutputDTO create(HabitInputDTO input);
    HabitOutputDTO update(Long id, HabitInputDTO input);
    boolean delete(Long id);
}
