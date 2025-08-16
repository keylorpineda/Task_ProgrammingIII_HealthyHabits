package task.healthyhabits.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import task.healthyhabits.security.hash.PasswordHashService;

@Service
@RequiredArgsConstructor
public class UserAuthenticationService {

    private final PasswordHashService passwordHashService;

    public boolean verifyCredentials(String rawPassword, String storedHash) {
        return passwordHashService.matches(rawPassword, storedHash);
    }
}
