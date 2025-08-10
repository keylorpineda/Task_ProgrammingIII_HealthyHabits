package task.healthyhabits.security.hash;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


  //Hasheo con validacion de politica y pepper opcional desde env

@Service
public class PasswordHashService {

    private final PasswordEncoder passwordEncoder;
    private final PasswordPolicy passwordPolicy;
    private final String pepper;

    public PasswordHashService(
            PasswordEncoder passwordEncoder,
            PasswordPolicy passwordPolicy,
            @Value("${security.password.pepper:}") String pepper) {
        this.passwordEncoder = passwordEncoder;
        this.passwordPolicy = passwordPolicy;
        this.pepper = (pepper == null) ? "" : pepper;
    }

    // Valida y hashea password cruda + pepper con BCrypt 
    public String encode(String rawPassword) {
        passwordPolicy.validate(rawPassword);
        String material = rawPassword + pepper;
        return passwordEncoder.encode(material);
    }

    // Compara password cruda + pepper contra el hash almacenado
    public boolean matches(String rawPassword, String storedEncodedPassword) {
        if (storedEncodedPassword == null || storedEncodedPassword.isBlank()) return false;
        String material = (rawPassword == null ? "" : rawPassword) + pepper;
        return passwordEncoder.matches(material, storedEncodedPassword);
    }
}
