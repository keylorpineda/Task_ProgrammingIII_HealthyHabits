package task.healthyhabits.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import task.healthyhabits.dtos.inputs.UserInputDTO;
import task.healthyhabits.dtos.outputs.UserOutputDTO;
import task.healthyhabits.models.User;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.services.UserAuthenticationService;
import task.healthyhabits.services.UserRegistrationService;

@Controller
@RequiredArgsConstructor
public class UserGraphQLController {

    private final UserRegistrationService registrationService;
    private final UserAuthenticationService authenticationService;
    private final UserRepository userRepository;

    @MutationMapping
    public UserOutputDTO registerUser(@Argument("input") UserInputDTO input) {
        return registrationService.register(input);
    }

    @MutationMapping
    public LoginResult login(@Argument("email") String email, @Argument("password") String password) {
        // Usa el método que sí exista en tu repo:
        // Si tienes findByEmail(...) -> deja esta línea.
        // Si tu repo expone findByEmailIgnoreCase(...), cambia la llamada.
        User user = userRepository.findByEmail(email).orElse(null);
        // User user = userRepository.findByEmailIgnoreCase(email).orElse(null);

        boolean ok = false;
        if (user != null && user.getPassword() != null) {
            ok = authenticationService.verifyCredentials(password, user.getPassword());
        }
        return new LoginResult(ok);
    }

    public record LoginResult(boolean ok) {}
}
