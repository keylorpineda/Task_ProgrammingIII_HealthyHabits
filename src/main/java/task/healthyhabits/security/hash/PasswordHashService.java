package task.healthyhabits.security.hash;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordHashService {

    private final PasswordEncoder passwordEncoder;
    private final PasswordPolicy passwordPolicy;
    private final String pepper;

    public PasswordHashService(
            PasswordEncoder passwordEncoder,
            PasswordPolicy passwordPolicy,
            @Value("${security.password.pepper:}") String pepper
    ) {
        this.passwordEncoder = passwordEncoder;
        this.passwordPolicy = passwordPolicy;
        this.pepper = (pepper == null) ? "" : pepper;
    }

    public String encode(String rawPassword) {
        passwordPolicy.validate(rawPassword);
        String material = (rawPassword == null ? "" : rawPassword) + pepper;
        return passwordEncoder.encode(material);
    }

    public boolean matches(String rawPassword, String storedEncodedPassword) {
        if (storedEncodedPassword == null || storedEncodedPassword.isBlank()) return false;
        String material = (rawPassword == null ? "" : rawPassword) + pepper;
        return passwordEncoder.matches(material, storedEncodedPassword);
    }
}
