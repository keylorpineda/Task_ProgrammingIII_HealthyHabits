package task.healthyhabits.configTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.util.ReflectionTestUtils;

import task.healthyhabits.config.AdminInitializer;
import task.healthyhabits.models.Role;
import task.healthyhabits.models.User;
import task.healthyhabits.repositories.RoleRepository;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.security.hash.PasswordHashService;
import org.springframework.core.env.Environment;

@ExtendWith({ MockitoExtension.class, OutputCaptureExtension.class })
class AdminInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordHashService passwordHashService;

    @Mock
    private Environment environment;

    @InjectMocks
    private AdminInitializer initializer;

    private final String adminEmail = "root@healthyhabits.com";
    private final String rawPassword = "SecurePass123!";
    private final String encodedPassword = "encoded-value";

    @BeforeEach
    void configureProperties() {
        ReflectionTestUtils.setField(initializer, "adminEmail", adminEmail);
    }

    @Test
    void runUpdatesExistingAdminWithNewPasswordAndRoles() {
        when(environment.getProperty("app.admin.password")).thenReturn(rawPassword);
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
        when(environment.getProperty("app.admin.password")).thenReturn(rawPassword);
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

    @Test
    void runLogsWarningAndSkipsWhenPasswordMissing(CapturedOutput output) {
        when(environment.getProperty("app.admin.password")).thenReturn("");

        when(environment.getProperty("APP_ADMIN_PASSWORD")).thenReturn(null);

        initializer.run();

        verifyNoInteractions(userRepository, roleRepository, passwordHashService);

        assertThat(output.getOut())
                .contains("Administrator account seeding skipped")
                .contains("app.admin.password")
                .contains("APP_ADMIN_PASSWORD");
    }

    @Test
    void runSeedsAdminUsingPropertyWhenEnvMissing() {
        when(environment.getProperty("app.admin.password")).thenReturn(rawPassword);

        Role role = new Role();
        List<Role> roles = List.of(role);

        when(roleRepository.findAll()).thenReturn(roles);
        when(passwordHashService.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        initializer.run();

        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(savedUser.capture());
        User admin = savedUser.getValue();
        assertThat(admin.getPassword()).isEqualTo(encodedPassword);
    }

    @Test
    void runSeedsAdminUsingEnvironmentWhenPropertyMissing() {
        when(environment.getProperty("app.admin.password")).thenReturn(null);
        when(environment.getProperty("APP_ADMIN_PASSWORD")).thenReturn(rawPassword);

        when(roleRepository.findAll()).thenReturn(List.of());
        when(passwordHashService.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        initializer.run();

        verify(passwordHashService).encode(rawPassword);
    }
}