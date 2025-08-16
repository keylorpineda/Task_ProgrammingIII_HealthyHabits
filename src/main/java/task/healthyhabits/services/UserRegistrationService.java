package task.healthyhabits.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.healthyhabits.dtos.inputs.UserInputDTO;
import task.healthyhabits.dtos.outputs.UserOutputDTO;
import task.healthyhabits.models.User;
import task.healthyhabits.models.Habit;
import task.healthyhabits.models.Role;
import task.healthyhabits.repositories.HabitRepository;
import task.healthyhabits.repositories.RoleRepository;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.security.hash.PasswordHashService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordHashService passwordHashService;
    private final RoleRepository roleRepository;
    private final HabitRepository habitRepository;

    @Transactional
    public UserOutputDTO register(UserInputDTO input) {
        if (input == null)
            throw new IllegalArgumentException("Input is required");

        if (userRepository.existsByEmail(input.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        String encoded = passwordHashService.encode(input.getPassword());

        User user = new User();
        user.setName(input.getName());
        user.setEmail(input.getEmail());
        user.setPassword(encoded);

        if (input.getRoles() != null) {
            List<Role> roles = input.getRoles().stream()
                    .map(r -> roleRepository.findByName(r.getName()).orElse(null))
                    .filter(Objects::nonNull)
                    .toList();
            user.setRoles(roles);
        } else {
            user.setRoles(Collections.emptyList());
        }

        if (input.getFavoriteHabits() != null) {
            List<Habit> habits = input.getFavoriteHabits().stream()
                    .map(h -> habitRepository.findByName(h.getName()).orElse(null))
                    .filter(Objects::nonNull)
                    .toList();
            user.setFavoriteHabits(habits);
        } else {
            user.setFavoriteHabits(Collections.emptyList());
        }

        user = userRepository.save(user);

        return new UserOutputDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRoles().stream()
                        .map(r -> new task.healthyhabits.dtos.outputs.RoleOutputDTO(r.getId(), r.getName(),
                                r.getPermissions()))
                        .toList(),
                user.getFavoriteHabits().stream()
                        .map(h -> new task.healthyhabits.dtos.outputs.HabitOutputDTO(h.getId(), h.getName(),
                                h.getCategory(), h.getDescription()))
                        .toList());
    }

    public String generateEncodedPasswordForRegistration(String plainPassword) {
        return passwordHashService.encode(plainPassword);
    }
}
