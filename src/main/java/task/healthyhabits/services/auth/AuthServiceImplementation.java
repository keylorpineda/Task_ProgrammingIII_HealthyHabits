package task.healthyhabits.services.auth;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.healthyhabits.dtos.inputs.UserInputDTO;
import task.healthyhabits.dtos.outputs.AuthTokenOutputDTO;
import task.healthyhabits.dtos.outputs.UserOutputDTO;
import task.healthyhabits.models.User;
import task.healthyhabits.models.Role;
import task.healthyhabits.models.Permission;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.repositories.RoleRepository;
import task.healthyhabits.security.JWT.JwtService;
import task.healthyhabits.security.hash.PasswordHashService;
import task.healthyhabits.services.auth.AuthService;
import task.healthyhabits.transformers.GenericMapperFactory;
import task.healthyhabits.transformers.InputOutputMapper;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthServiceImplementation implements AuthService {

    private final UserRepository userRepository;
    private final PasswordHashService passwordHashService;
    private final JwtService jwtService;
    private final GenericMapperFactory mapperFactory;
    private final RoleRepository roleRepository;
    private static final Logger logger = LogManager.getLogger(AuthServiceImplementation.class);

    @Override
    @Transactional
    public AuthTokenOutputDTO register(UserInputDTO input) {
        logger.info("Registering new user");
        if (userRepository.existsByEmail(input.getEmail())) {
            logger.warn("Email already registered");
            throw new IllegalArgumentException("Email already registered");
        }
        InputOutputMapper<UserInputDTO, User, UserOutputDTO> io = mapperFactory
                .createInputOutputMapper(UserInputDTO.class, User.class, UserOutputDTO.class);
        User user = io.convertFromInput(input);
        user.setPassword(passwordHashService.encode(input.getPassword()));
        user = userRepository.save(user);
        Role defaultRole = roleRepository.findByPermission(Permission.USER_READ)
                .orElseThrow(() -> new IllegalStateException("Default role not found"));
        user.setRoles(java.util.Collections.singletonList(defaultRole));
        user = userRepository.save(user);
        String token = jwtService.generateToken(user.getEmail());
        Date exp = jwtService.extractExpiration(token);
        OffsetDateTime expiresAt = OffsetDateTime.ofInstant(exp.toInstant(), ZoneOffset.UTC);
        UserOutputDTO outUser = io.convertToOutput(user);
        return new AuthTokenOutputDTO(token, expiresAt, outUser);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthTokenOutputDTO login(String email, String password) {
        logger.info("Login attempt");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Invalid credentials");
                    return new BadCredentialsException("Invalid credentials");
                });
        if (!passwordHashService.matches(password, user.getPassword())) {
            logger.warn("Invalid credentials");
            throw new BadCredentialsException("Invalid credentials");
        }
        String token = jwtService.generateToken(user.getEmail());
        Date exp = jwtService.extractExpiration(token);
        OffsetDateTime expiresAt = OffsetDateTime.ofInstant(exp.toInstant(), ZoneOffset.UTC);
        InputOutputMapper<UserInputDTO, User, UserOutputDTO> io = mapperFactory
                .createInputOutputMapper(UserInputDTO.class, User.class, UserOutputDTO.class);
        return new AuthTokenOutputDTO(token, expiresAt, io.convertToOutput(user));
    }

    @Override
    @Transactional(readOnly = true)
    public AuthTokenOutputDTO verifyToken(String token) {
        String subject = jwtService.extractUsername(token);
        if (subject == null || !jwtService.isTokenValid(token, subject)) {
            return null;
        }
        User user = userRepository.findByEmail(subject).orElse(null);
        if (user == null) {
            return null;
        }
        OffsetDateTime expiresAt = OffsetDateTime.ofInstant(jwtService.extractExpiration(token).toInstant(),
                ZoneOffset.UTC);
        UserOutputDTO userDto = mapperFactory.createMapper(User.class, UserOutputDTO.class)
                .convertToDTO(user);
        return new AuthTokenOutputDTO(token, expiresAt, userDto);
    }
}
