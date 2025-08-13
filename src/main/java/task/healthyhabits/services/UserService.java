package task.healthyhabits.services;

import task.healthyhabits.models.User;
import task.healthyhabits.models.Habit;
import task.healthyhabits.models.Role;
import task.healthyhabits.models.AuthToken;

import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.repositories.AuthTokenRepository;
import task.healthyhabits.mappers.MapperForUser;
import task.healthyhabits.mappers.MapperForHabit;
import task.healthyhabits.mappers.MapperForRole;
import task.healthyhabits.dtos.inputs.UserInputDTO;
import task.healthyhabits.dtos.normals.UserDTO;
import task.healthyhabits.dtos.normals.HabitDTO;
import task.healthyhabits.dtos.outputs.UserOutputDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final AuthTokenRepository authTokenRepository;

    public UserService(UserRepository userRepository, AuthTokenRepository authTokenRepository) {
        this.userRepository = userRepository;
        this.authTokenRepository = authTokenRepository;
    }

    @Transactional(readOnly = true)
    public UserDTO me(String token) {
        AuthToken at = authTokenRepository.findById(token)
                .orElseThrow(() -> new NoSuchElementException("Invalid token"));
        return MapperForUser.toDTO(at.getUser());
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> list(Pageable pageable) {
        return userRepository.findAll(pageable).map(MapperForUser::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<HabitDTO> myFavorites(Long userId, Pageable pageable) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        List<HabitDTO> all = u.getFavoriteHabits().stream()
                .map(MapperForHabit::toDTO)
                .collect(Collectors.toList());

        return paginate(all, pageable);
    }

    public UserOutputDTO create(UserInputDTO input) {
        User user = MapperForUser.toModel(input);

        if (user.getRoles() == null)
            user.setRoles(new ArrayList<Role>());
        if (user.getFavoriteHabits() == null)
            user.setFavoriteHabits(new ArrayList<Habit>());

        user = userRepository.save(user);
        return MapperForUser.toOutput(user);
    }

    public UserOutputDTO update(Long id, UserInputDTO input) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        user.setName(input.getName());
        user.setEmail(input.getEmail());
        user.setPassword(input.getPassword());

        List<Role> roles = (input.getRoles() == null) ? new ArrayList<>()
                : input.getRoles().stream().map(MapperForRole::toModel).collect(Collectors.toList());
        user.setRoles(roles);

        List<Habit> favs = (input.getFavoriteHabits() == null) ? new ArrayList<>()
                : input.getFavoriteHabits().stream().map(MapperForHabit::toModel).collect(Collectors.toList());
        user.setFavoriteHabits(favs);

        user = userRepository.save(user);
        return MapperForUser.toOutput(user);
    }

    public boolean delete(Long id) {
        if (!userRepository.existsById(id))
            return false;
        userRepository.deleteById(id);
        return true;
    }

    private <T> Page<T> paginate(List<T> all, Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int size = pageable.getPageSize();

        if (offset >= all.size()) {
            return new PageImpl<>(List.of(), pageable, all.size());
        }
        List<T> content = all.stream()
                .skip(offset)
                .limit(size)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, all.size());
    }
}