package task.healthyhabits.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import task.healthyhabits.models.User;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import task.healthyhabits.models.Role;
import task.healthyhabits.repositories.RoleRepository;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.security.hash.PasswordHashService;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Order(Ordered.LOWEST_PRECEDENCE)
public class AdminInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminInitializer.class);
    private static final String ADMIN_PASSWORD_ENV = "APP_ADMIN_PASSWORD";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHashService passwordHashService;
    private final Environment environment;

    @Value("${app.admin.email:admin@healthyhabits.com}")
    private String adminEmail;

    @Override
    public void run(String... args) {
        String adminPassword = environment.getProperty("app.admin.password");
        String envPassword = environment.getProperty(ADMIN_PASSWORD_ENV);

        if (!StringUtils.hasText(envPassword)) {
            LOGGER.error("Environment variable {} must be provided with a secure administrator password.",
                    ADMIN_PASSWORD_ENV);
            throw new IllegalStateException("Environment variable APP_ADMIN_PASSWORD is required.");
        }

        if (!StringUtils.hasText(adminPassword)) {
            LOGGER.error("Property 'app.admin.password' is empty. Ensure environment variable {} is defined.",
                    ADMIN_PASSWORD_ENV);
            throw new IllegalStateException("Property 'app.admin.password' must not be empty.");
        }

        if (!envPassword.equals(adminPassword)) {
            LOGGER.error("The configured administrator password does not originate from environment variable {}.",
                    ADMIN_PASSWORD_ENV);
            throw new IllegalStateException(
                    "Administrator password must be provided via environment variable APP_ADMIN_PASSWORD.");
        }

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
