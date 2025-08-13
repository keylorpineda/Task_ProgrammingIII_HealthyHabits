package task.healthyhabits.services;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import task.healthyhabits.security.hash.PasswordHashService;

//Genera el hash listo para persistir cuando ya se conecte bien la BD

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final PasswordHashService passwordHashService;

    public String generateEncodedPasswordForRegistration(String plainPassword) {
        return passwordHashService.encode(plainPassword);
    }
}
