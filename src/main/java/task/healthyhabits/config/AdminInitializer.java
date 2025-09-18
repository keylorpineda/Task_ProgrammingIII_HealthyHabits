package task.healthyhabits.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import task.healthyhabits.models.User;
import task.healthyhabits.repositories.RoleRepository;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.security.hash.PasswordHashService;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHashService passwordHashService;

    @Value("${app.admin.email:admin@healthyhabits.com}")
    private String adminEmail;

    @Value("${app.admin.password:admin}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setName("Administrator");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordHashService.encode(adminPassword));
            admin.setRoles(roleRepository.findAll());
            userRepository.save(admin);
        }
    }
}