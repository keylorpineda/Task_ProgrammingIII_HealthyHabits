package task.healthyhabits.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.healthyhabits.dtos.inputs.UserInputDTO;
import task.healthyhabits.dtos.outputs.UserOutputDTO;
import task.healthyhabits.models.User;
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
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordHashService passwordHashService;

    @Transactional
    public UserOutputDTO register(UserInputDTO input) {

        if (userRepository.findByEmail(input.getEmail()).isPresent()) {

    private final RoleRepository roleRepository;
    private final HabitRepository habitRepository;
    private final PasswordHashService passwordHashService;

    
    @Transactional
    public UserOutputDTO register(UserInputDTO input) {
        if (userRepository.existsByEmail(input.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        String encoded = passwordHashService.encode(input.getPassword());

        User user = new User();
        user.setName(input.getName());
        user.setEmail(input.getEmail());
        user.setPassword(encoded); 

        return new UserOutputDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

        if (input.getRoles() != null) {
            List<Role> roles = input.getRoles().stream()
                    .map(r -> roleRepository.findByName(r.getName()).orElse(null))
                    .filter(Objects::nonNull)
                    .toList();
            user.setRoles(roles);
        }

        user.setFavoriteHabits(Collections.emptyList());

        user = userRepository.save(user);

        return toOutputDTO(user);
    }
    public String generateEncodedPasswordForRegistration(String plainPassword) {
        return passwordHashService.encode(plainPassword);
    }


    private UserOutputDTO toOutputDTO(User user) {
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
