package task.healthyhabits.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import task.healthyhabits.dtos.inputs.UserInputDTO;
import task.healthyhabits.dtos.outputs.HabitOutputDTO;
import task.healthyhabits.dtos.outputs.RoleOutputDTO;
import task.healthyhabits.dtos.outputs.UserOutputDTO;

import task.healthyhabits.models.Habit;
import task.healthyhabits.models.Role;
import task.healthyhabits.models.User;

import task.healthyhabits.repositories.HabitRepository;
import task.healthyhabits.repositories.RoleRepository;
import task.healthyhabits.repositories.UserRepository;

import task.healthyhabits.security.hash.PasswordHashService;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HabitRepository habitRepository;
    private final PasswordHashService passwordHashService;

    @Transactional
    public UserOutputDTO register(UserInputDTO input) {
        if (userRepository.existsByEmail(input.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setName(input.getName());
        user.setEmail(input.getEmail());
        user.setPassword(passwordHashService.encode(input.getPassword()));

        // Mapear roles (find-or-create por nombre)
        if (input.getRoles() != null && !input.getRoles().isEmpty()) {
            List<Role> roles = input.getRoles().stream()
                    .map(rdto -> roleRepository.findByName(rdto.getName())
                            .orElseGet(() -> {
                                Role r = new Role();
                                r.setName(rdto.getName());
                                r.setPermissions(rdto.getPermissions());
                                return roleRepository.save(r);
                            }))
                    .toList();
            user.setRoles(roles);
        }

        // Mapear hábitos favoritos (crear a partir del DTO)
        if (input.getFavoriteHabits() != null && !input.getFavoriteHabits().isEmpty()) {
            List<Habit> habits = input.getFavoriteHabits().stream()
                    .map(hdto -> {
                        Habit h = new Habit();
                        h.setName(hdto.getName());
                        h.setCategory(hdto.getCategory());
                        h.setDescription(hdto.getDescription());
                        return habitRepository.save(h);
                    })
                    .toList();
            user.setFavoriteHabits(habits);
        }

        user = userRepository.save(user);

        return new UserOutputDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                toRoleOutputs(user.getRoles()),
                toHabitOutputs(user.getFavoriteHabits())
        );
    }

    private List<RoleOutputDTO> toRoleOutputs(List<Role> roles) {
        if (roles == null) return Collections.emptyList();
        return roles.stream()
                .map(r -> new RoleOutputDTO(r.getId(), r.getName(), r.getPermissions()))
                .toList();
    }

    private List<HabitOutputDTO> toHabitOutputs(List<Habit> habits) {
        if (habits == null) return Collections.emptyList();
        return habits.stream()
                .map(h -> new HabitOutputDTO(h.getId(), h.getName(), h.getCategory(), h.getDescription()))
                .toList();
    }
}
