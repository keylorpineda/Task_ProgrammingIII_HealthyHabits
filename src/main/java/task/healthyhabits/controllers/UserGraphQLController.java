package task.healthyhabits.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import task.healthyhabits.dtos.inputs.UserInputDTO;
import task.healthyhabits.dtos.outputs.UserOutputDTO;
import task.healthyhabits.models.User;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.security.JWT.JwtService;
import task.healthyhabits.services.UserAuthenticationService;
import task.healthyhabits.services.UserRegistrationService;

@Controller
@RequiredArgsConstructor
public class UserGraphQLController {

    private final UserRegistrationService registrationService;
    private final UserAuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @MutationMapping
    public UserOutputDTO registerUser(@Argument("input") UserInputDTO input) {
        return registrationService.register(input);
    }

    @MutationMapping
    public AuthPayload login(@Argument("email") String email, @Argument("password") String password) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null || user.getPassword() == null) {
            return new AuthPayload(false, null, null);
        }

        boolean ok = authenticationService.verifyCredentials(password, user.getPassword());
        if (!ok) {
            return new AuthPayload(false, null, null);
        }

        String token = jwtService.generateToken(user.getEmail());
        return new AuthPayload(true, token, user.getEmail());
    }

    public static class AuthPayload {
        private boolean ok;
        private String token;
        private String email;
        public AuthPayload() {}
        public AuthPayload(boolean ok, String token, String email) {
            this.ok = ok; this.token = token; this.email = email;
        }
        public boolean isOk() { return ok; }
        public String getToken() { return token; }
        public String getEmail() { return email; }
        public void setOk(boolean ok) { this.ok = ok; }
        public void setToken(String token) { this.token = token; }
        public void setEmail(String email) { this.email = email; }
    }
}
