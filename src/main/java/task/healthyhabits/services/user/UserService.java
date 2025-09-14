package task.healthyhabits.services.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import task.healthyhabits.dtos.inputs.UserInputDTO;
import task.healthyhabits.dtos.normals.HabitDTO;
import task.healthyhabits.dtos.normals.UserDTO;
import task.healthyhabits.dtos.outputs.UserOutputDTO;

public interface UserService {
    UserDTO getCurrentUser();

    UserDTO getAuthenticatedUser();

    Page<UserDTO> list(Pageable pageable);

    Page<HabitDTO> listMyFavoriteHabits(Long userId, Pageable pageable);

    UserOutputDTO create(UserInputDTO input);

    UserOutputDTO update(Long id, UserInputDTO input);

    boolean delete(Long id);

    Page<UserDTO> listStudentsOfCoach(Long coachId, Pageable pageable);
}
