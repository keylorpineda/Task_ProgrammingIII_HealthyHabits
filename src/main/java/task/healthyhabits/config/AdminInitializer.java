package task.healthyhabits.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import task.healthyhabits.models.User;
import task.healthyhabits.repositories.RoleRepository;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.security.hash.PasswordHashService;
import java.util.ArrayList;
import java.util.List;
import task.healthyhabits.models.Role;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@RequiredArgsConstructor
@Order(Ordered.LOWEST_PRECEDENCE)
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHashService passwordHashService;

    @Value("${app.admin.email:admin@healthyhabits.com}")
    private String adminEmail;

  @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
           Assert.hasText(adminPassword, "Property 'app.admin.password' must not be empty");

        String encodedPassword = passwordHashService.encode(adminPassword);
        List<Role> roles = roleRepository.findAll();

        User admin = userRepository.findByEmail(adminEmail)
                .map(existing -> {
                    existing.setPassword(encodedPassword);
                    existing.setRoles(new ArrayList<>(roles));
                    return existing;
                })
                .orElseGet(() -> {
                    User newAdmin = new User();
                    newAdmin.setName("Administrator");
                    newAdmin.setEmail(adminEmail);
                    newAdmin.setPassword(encodedPassword);
                    newAdmin.setRoles(new ArrayList<>(roles));
                    return newAdmin;
                });

        userRepository.save(admin);
    }
}
