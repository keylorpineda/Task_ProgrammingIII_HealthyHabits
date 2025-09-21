package task.healthyhabits.services.auth;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import task.healthyhabits.dtos.inputs.UserInputDTO;
import task.healthyhabits.dtos.outputs.AuthTokenOutputDTO;
import task.healthyhabits.dtos.outputs.UserOutputDTO;
import task.healthyhabits.models.Permission;
import task.healthyhabits.models.Role;
import task.healthyhabits.models.User;
import task.healthyhabits.repositories.RoleRepository;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.security.JWT.JwtService;
import task.healthyhabits.security.hash.PasswordHashService;
import task.healthyhabits.transformers.GenericMapper;
import task.healthyhabits.transformers.GenericMapperFactory;
import task.healthyhabits.transformers.InputOutputMapper;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplementationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHashService passwordHashService;

    @Mock
    private JwtService jwtService;

    @Mock
    private GenericMapperFactory mapperFactory;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private InputOutputMapper<UserInputDTO, User, UserOutputDTO> inputOutputMapper;

    @Mock
    private GenericMapper<User, UserOutputDTO> userMapper;

    @InjectMocks
    private AuthServiceImplementation authService;

    @Test
    void registerCreatesUserAssignsDefaultRoleAndReturnsToken() {
        UserInputDTO input = new UserInputDTO("Alice", "alice@example.com", "Passw0rd!", List.of(), List.of(), null);
        User user = new User(null, "Alice", "alice@example.com", null, null, List.of(), null);
        Role defaultRole = new Role(10L, "Default", Permission.USER_READ);
        Date expiration = new Date(System.currentTimeMillis() + 60000L);
        UserOutputDTO outputDTO = new UserOutputDTO(1L, "Alice", "alice@example.com", List.of(), List.of(), null);

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(mapperFactory.createInputOutputMapper(UserInputDTO.class, User.class, UserOutputDTO.class))
                .thenReturn(inputOutputMapper);
        when(inputOutputMapper.convertFromInput(input)).thenReturn(user);
        when(passwordHashService.encode("Passw0rd!")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            if (saved.getId() == null) {
                saved.setId(1L);
            }
            return saved;
        });
        when(roleRepository.findByPermission(Permission.USER_READ)).thenReturn(Optional.of(defaultRole));
        when(jwtService.generateToken("alice@example.com")).thenReturn("token");
        when(jwtService.extractExpiration("token")).thenReturn(expiration);
        when(inputOutputMapper.convertToOutput(user)).thenReturn(outputDTO);

        AuthTokenOutputDTO result = authService.register(input);

        assertThat(result.getToken()).isEqualTo("token");
        assertThat(result.getExpiresAt()).isEqualTo(OffsetDateTime.ofInstant(expiration.toInstant(), ZoneOffset.UTC));
        assertThat(result.getUser()).isEqualTo(outputDTO);
        assertThat(user.getPassword()).isEqualTo("hashed");
        assertThat(user.getRoles()).containsExactly(defaultRole);
        verify(passwordHashService).encode("Passw0rd!");
        verify(userRepository, times(2)).save(user);
    }

    @Test
    void registerFailsWhenEmailAlreadyRegistered() {
        UserInputDTO input = new UserInputDTO("Alice", "alice@example.com", "Passw0rd!", List.of(), List.of(), null);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already registered");
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerFailsWhenDefaultRoleMissing() {
        UserInputDTO input = new UserInputDTO("Alice", "alice@example.com", "Passw0rd!", List.of(), List.of(), null);
        User user = new User(null, "Alice", "alice@example.com", null, null, List.of(), null);

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(mapperFactory.createInputOutputMapper(UserInputDTO.class, User.class, UserOutputDTO.class))
                .thenReturn(inputOutputMapper);
        when(inputOutputMapper.convertFromInput(input)).thenReturn(user);
        when(passwordHashService.encode("Passw0rd!")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(roleRepository.findByPermission(Permission.USER_READ)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.register(input))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Default role not found");
    }

    @Test
    void loginReturnsTokenWhenCredentialsValid() {
        User user = new User(2L, "Alice", "alice@example.com", "hashed", Collections.emptyList(), Collections.emptyList(), null);
        UserOutputDTO outputDTO = new UserOutputDTO(2L, "Alice", "alice@example.com", List.of(), List.of(), null);
        Date expiration = new Date(System.currentTimeMillis() + 60000L);

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(passwordHashService.matches("Passw0rd!", "hashed")).thenReturn(true);
        when(jwtService.generateToken("alice@example.com")).thenReturn("token");
        when(jwtService.extractExpiration("token")).thenReturn(expiration);
        when(mapperFactory.createInputOutputMapper(UserInputDTO.class, User.class, UserOutputDTO.class))
                .thenReturn(inputOutputMapper);
        when(inputOutputMapper.convertToOutput(user)).thenReturn(outputDTO);

        AuthTokenOutputDTO result = authService.login("alice@example.com", "Passw0rd!");

        assertThat(result.getToken()).isEqualTo("token");
        assertThat(result.getExpiresAt()).isEqualTo(OffsetDateTime.ofInstant(expiration.toInstant(), ZoneOffset.UTC));
        assertThat(result.getUser()).isEqualTo(outputDTO);
    }

    @Test
    void loginThrowsWhenUserNotFound() {
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login("alice@example.com", "Passw0rd!"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void loginThrowsWhenPasswordDoesNotMatch() {
        User user = new User(2L, "Alice", "alice@example.com", "hashed", Collections.emptyList(), Collections.emptyList(), null);
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(passwordHashService.matches("Passw0rd!", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login("alice@example.com", "Passw0rd!"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void verifyTokenReturnsNullWhenSubjectMissing() {
        when(jwtService.extractUsername("token")).thenReturn(null);

        AuthTokenOutputDTO result = authService.verifyToken("token");

        assertThat(result).isNull();
        verify(jwtService, never()).isTokenValid(any(), any());
    }

    @Test
    void verifyTokenReturnsNullWhenTokenInvalid() {
        when(jwtService.extractUsername("token")).thenReturn("alice@example.com");
        when(jwtService.isTokenValid("token", "alice@example.com")).thenReturn(false);

        AuthTokenOutputDTO result = authService.verifyToken("token");

        assertThat(result).isNull();
    }

    @Test
    void verifyTokenReturnsNullWhenUserMissing() {
        when(jwtService.extractUsername("token")).thenReturn("alice@example.com");
        when(jwtService.isTokenValid("token", "alice@example.com")).thenReturn(true);
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.empty());

        AuthTokenOutputDTO result = authService.verifyToken("token");

        assertThat(result).isNull();
    }

    @Test
    void verifyTokenReturnsDtoWhenValid() {
        User user = new User(3L, "Alice", "alice@example.com", "hashed", Collections.emptyList(), Collections.emptyList(), null);
        UserOutputDTO outputDTO = new UserOutputDTO(3L, "Alice", "alice@example.com", List.of(), List.of(), null);
        Date expiration = new Date(System.currentTimeMillis() + 60000L);

        when(jwtService.extractUsername("token")).thenReturn("alice@example.com");
        when(jwtService.isTokenValid("token", "alice@example.com")).thenReturn(true);
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(jwtService.extractExpiration("token")).thenReturn(expiration);
        when(mapperFactory.createMapper(User.class, UserOutputDTO.class)).thenReturn(userMapper);
        when(userMapper.convertToDTO(user)).thenReturn(outputDTO);

        AuthTokenOutputDTO result = authService.verifyToken("token");

        assertThat(result.getToken()).isEqualTo("token");
        assertThat(result.getExpiresAt()).isEqualTo(OffsetDateTime.ofInstant(expiration.toInstant(), ZoneOffset.UTC));
        assertThat(result.getUser()).isEqualTo(outputDTO);
    }
}