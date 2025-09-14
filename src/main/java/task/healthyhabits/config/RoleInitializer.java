package task.healthyhabits.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import task.healthyhabits.models.Permission;
import task.healthyhabits.models.Role;
import task.healthyhabits.repositories.RoleRepository;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        for (Permission permission : Permission.values()) {
            roleRepository.findByPermission(permission)
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setPermission(permission);
                        role.setName(permission.name());
                        return roleRepository.save(role);
                    });
        }
    }
}