package task.healthyhabits.services.guide;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import task.healthyhabits.dtos.inputs.GuideInputDTO;
import task.healthyhabits.dtos.normals.GuideDTO;
import task.healthyhabits.dtos.outputs.GuideOutputDTO;
import task.healthyhabits.models.Category;

public interface GuideService {
        Page<GuideDTO> list(Pageable pageable);

        Page<GuideDTO> listByObjective(String objective, Pageable pageable);

        GuideDTO findByIdOrNull(Long id);

        Page<GuideDTO> recommended(Category category, Long forUserId, Pageable pageable);

        GuideOutputDTO create(GuideInputDTO input);

        GuideOutputDTO update(Long id, GuideInputDTO input);

        boolean delete(Long id);
}
