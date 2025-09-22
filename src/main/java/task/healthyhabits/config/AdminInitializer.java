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
import org.slf4j.helpers.MessageFormatter;

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
            logSkipWarning(
                    "Administrator account seeding skipped: environment variable {} is not defined or blank. "
                            + "Provide matching values for property 'app.admin.password' and environment variable {} "
                            + "to seed the administrator account.",
                    ADMIN_PASSWORD_ENV, ADMIN_PASSWORD_ENV);
            return;
        }

        if (!StringUtils.hasText(adminPassword)) {
            logSkipWarning(
                    "Administrator account seeding skipped: property 'app.admin.password' is not defined or blank. "
                            + "Provide matching values for property 'app.admin.password' and environment variable {} "
                            + "to seed the administrator account.",
                    ADMIN_PASSWORD_ENV);
            return;
        }

        if (!envPassword.equals(adminPassword)) {
            logSkipWarning(
                    "Administrator account seeding skipped: property 'app.admin.password' does not match environment "
                            + "variable {}. Ensure both are configured with the same secure password to seed the "
                            + "administrator account.",
                    ADMIN_PASSWORD_ENV);
            return;
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

    private void logSkipWarning(String message, Object... args) {
        LOGGER.warn(message, args);
        System.out.println(MessageFormatter.arrayFormat(message, args).getMessage());
    }
}
