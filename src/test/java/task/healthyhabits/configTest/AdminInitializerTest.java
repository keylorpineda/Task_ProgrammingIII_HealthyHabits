package task.healthyhabits.configTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import task.healthyhabits.config.AdminInitializer;
import task.healthyhabits.models.Role;
import task.healthyhabits.models.User;
import task.healthyhabits.repositories.RoleRepository;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.security.hash.PasswordHashService;

@ExtendWith(MockitoExtension.class)
class AdminInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordHashService passwordHashService;

    @InjectMocks
    private AdminInitializer initializer;

    private final String adminEmail = "root@healthyhabits.com";
    private final String rawPassword = "SecurePass123!";
    private final String encodedPassword = "encoded-value";

    @BeforeEach
    void configureProperties() {
        ReflectionTestUtils.setField(initializer, "adminEmail", adminEmail);
        ReflectionTestUtils.setField(initializer, "adminPassword", rawPassword);
    }

    @Test
    void runUpdatesExistingAdminWithNewPasswordAndRoles() {
        Role role = new Role();
        List<Role> roles = List.of(role);
        User existing = new User();
        existing.setPassword("old-password");

        when(roleRepository.findAll()).thenReturn(roles);
        when(passwordHashService.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        initializer.run();

        assertThat(existing.getPassword()).isEqualTo(encodedPassword);
        assertThat(existing.getRoles()).containsExactlyElementsOf(roles);
        verify(userRepository).save(existing);
    }

    @Test
    void runCreatesAdminWhenMissing() {
        Role role = new Role();
        role.setName("MANAGER");
        List<Role> roles = List.of(role);

        when(roleRepository.findAll()).thenReturn(roles);
        when(passwordHashService.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);

        initializer.run();

        verify(userRepository).save(savedUser.capture());
        User admin = savedUser.getValue();
        assertThat(admin.getName()).isEqualTo("Administrator");
        assertThat(admin.getEmail()).isEqualTo(adminEmail);
        assertThat(admin.getPassword()).isEqualTo(encodedPassword);
        assertThat(admin.getRoles()).containsExactlyElementsOf(roles);
    }
}