package task.healthyhabits.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.healthyhabits.dtos.inputs.UserInputDTO;
import task.healthyhabits.dtos.outputs.UserOutputDTO;
import task.healthyhabits.models.User;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.security.hash.PasswordHashService;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordHashService passwordHashService;

    @Transactional
    public UserOutputDTO register(UserInputDTO input) {

        if (userRepository.findByEmail(input.getEmail()).isPresent()) {
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

    public String generateEncodedPasswordForRegistration(String plainPassword) {
        return passwordHashService.encode(plainPassword);
    }
}
