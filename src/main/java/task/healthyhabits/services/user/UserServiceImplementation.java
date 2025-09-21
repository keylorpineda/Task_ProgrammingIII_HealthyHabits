package task.healthyhabits.services.user;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.healthyhabits.dtos.inputs.HabitInputDTO;
import task.healthyhabits.dtos.inputs.RoleInputDTO;
import task.healthyhabits.dtos.inputs.UserInputDTO;
import task.healthyhabits.dtos.normals.HabitDTO;
import task.healthyhabits.dtos.normals.UserDTO;
import task.healthyhabits.dtos.outputs.UserOutputDTO;
import task.healthyhabits.models.*;
import task.healthyhabits.repositories.*;
import task.healthyhabits.security.hash.PasswordHashService;
import task.healthyhabits.transformers.GenericMapperFactory;
import task.healthyhabits.transformers.InputOutputMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImplementation.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HabitRepository habitRepository;
    private final PasswordHashService passwordHashService;
    private final GenericMapperFactory mapperFactory;

    @Override
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser() {
        return getAuthenticatedUser();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            logger.warn("Missing authentication when fetching current user");
            throw new NoSuchElementException("Missing authentication");
        }
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found for authentication subject {}", email);
                    return new NoSuchElementException("User not found for authentication subject");
                });
        return mapperFactory.createMapper(User.class, UserDTO.class).convertToDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> list(Pageable pageable) {
        logger.info("Listing users with pageable {}", pageable);
        try {
            Page<UserDTO> users = userRepository.findAll(pageable)
                    .map(e -> mapperFactory.createMapper(User.class, UserDTO.class).convertToDTO(e));
            logger.info("Listed {} users", users.getNumberOfElements());
            return users;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error listing users with pageable {}", pageable, ex);
            throw ex;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HabitDTO> listMyFavoriteHabits(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            logger.warn("User {} not found when listing favorite habits", userId);
            throw new NoSuchElementException("User not found");
        }

        return userRepository.findFavoriteHabitsByUserId(userId, pageable)
                .map(h -> mapperFactory.createMapper(Habit.class, HabitDTO.class).convertToDTO(h));
    }

    @Override
    @Transactional
    public UserOutputDTO create(UserInputDTO input) {
        logger.info("Creating user with email {}", input.getEmail());
        try {
            if (userRepository.existsByEmail(input.getEmail())) {
                logger.warn("Email {} already registered", input.getEmail());
                throw new IllegalArgumentException("Email already registered");
            }
            InputOutputMapper<UserInputDTO, User, UserOutputDTO> io = mapperFactory
                    .createInputOutputMapper(UserInputDTO.class, User.class, UserOutputDTO.class);
            User user = io.convertFromInput(input);
            user.setPassword(passwordHashService.encode(input.getPassword()));
            user.setRoles(resolveRolesInline(input));
            user.setFavoriteHabits(resolveHabitsInline(input));
            if (input.getCoachId() != null) {
                userRepository.findById(input.getCoachId())
                        .ifPresentOrElse(user::setCoach, () -> logger.warn("Coach {} not found for user creation", input.getCoachId()));
            }
            user = userRepository.save(user);
            UserOutputDTO output = io.convertToOutput(user);
            logger.info("Created user {} with email {}", user.getId(), user.getEmail());
            return output;
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error creating user with email {}", input.getEmail(), ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public UserOutputDTO update(Long id, UserInputDTO input) {
        logger.info("Updating user {} with input {}", id, input);
        try {
            InputOutputMapper<UserInputDTO, User, UserOutputDTO> io = mapperFactory
                    .createInputOutputMapper(UserInputDTO.class, User.class, UserOutputDTO.class);
            User user = userRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("User {} not found for update", id);
                        return new NoSuchElementException("User not found");
                    });

            if (input.getName() != null)
                user.setName(input.getName());
            if (input.getEmail() != null && !input.getEmail().equals(user.getEmail())) {
                if (userRepository.existsByEmail(input.getEmail())) {
                    logger.warn("Email {} already in use for another user", input.getEmail());
                    throw new IllegalArgumentException("Email already in use");
                }
                user.setEmail(input.getEmail());
            }
            if (input.getPassword() != null && !input.getPassword().isBlank()) {
                user.setPassword(passwordHashService.encode(input.getPassword()));
            }
            if (input.getRoles() != null)
                user.setRoles(resolveRolesInline(input));
            if (input.getFavoriteHabits() != null)
                user.setFavoriteHabits(resolveHabitsInline(input));
            if (input.getCoachId() != null) {
                if (input.getCoachId() == 0) {
                    user.setCoach(null);
                } else {
                    User coach = userRepository.findById(input.getCoachId())
                            .orElseThrow(() -> {
                                logger.warn("Coach {} not found for user update", input.getCoachId());
                                return new NoSuchElementException("Coach not found");
                            });
                    user.setCoach(coach);
                }
            }
            user = userRepository.save(user);
            UserOutputDTO output = io.convertToOutput(user);
            logger.info("Updated user {} successfully", id);
            return output;
        } catch (IllegalArgumentException | NoSuchElementException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error updating user {}", id, ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        logger.info("Deleting user {}", id);
        try {
            if (!userRepository.existsById(id)) {
                logger.warn("User {} not found for deletion", id);
                return false;
            }
            userRepository.deleteById(id);
            logger.info("Deleted user {}", id);
            return true;
        } catch (RuntimeException ex) {
            logger.error("Unexpected error deleting user {}", id, ex);
            throw ex;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> listStudentsOfCoach(Long coachId, Pageable pageable) {
        return userRepository.findAllByCoachId(coachId, pageable)
                .map(e -> mapperFactory.createMapper(User.class, UserDTO.class).convertToDTO(e));
    }

    private List<Role> resolveRolesInline(UserInputDTO input) {
        List<Role> out = new ArrayList<>();
        if (input.getRoles() == null)
            return out;
        for (RoleInputDTO rIn : input.getRoles()) {
            Permission perm = rIn.getPermission();
            Optional<Role> existing = roleRepository.findByPermission(perm);
            if (existing.isPresent())
                out.add(existing.get());
            else {
                Role r = new Role();
                r.setPermission(perm);
                r.setName((rIn.getName() == null || rIn.getName().isBlank()) ? perm.name() : rIn.getName());
                out.add(roleRepository.save(r));
            }
        }
        return out;
    }

    private List<Habit> resolveHabitsInline(UserInputDTO input) {
        List<Habit> out = new ArrayList<>();
        if (input.getFavoriteHabits() == null)
            return out;
        for (HabitInputDTO hIn : input.getFavoriteHabits()) {
            String name = hIn.getName() == null ? "" : hIn.getName().trim();
            Category category = hIn.getCategory();
            Optional<Habit> existing = habitRepository.findByNameAndCategory(name, category);
            Habit habit = existing.orElseGet(() -> {
                Habit nh = new Habit();
                nh.setName(name);
                nh.setCategory(category);
                nh.setDescription(hIn.getDescription());
                return habitRepository.save(nh);
            });
            out.add(habit);
        }
        return out;
    }
}
