package task.healthyhabits.servicesTest;
  
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import task.healthyhabits.dtos.inputs.HabitInputDTO;
import task.healthyhabits.dtos.inputs.RoleInputDTO;
import task.healthyhabits.dtos.inputs.UserInputDTO;
import task.healthyhabits.dtos.normals.HabitDTO;
import task.healthyhabits.dtos.normals.UserDTO;
import task.healthyhabits.dtos.outputs.UserOutputDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.Habit;
import task.healthyhabits.models.Permission;
import task.healthyhabits.models.Role;
import task.healthyhabits.models.User;
import task.healthyhabits.repositories.HabitRepository;
import task.healthyhabits.repositories.RoleRepository;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.security.hash.PasswordHashService;
import task.healthyhabits.services.user.UserServiceImplementation;
import task.healthyhabits.transformers.GenericMapper;
import task.healthyhabits.transformers.GenericMapperFactory;
import task.healthyhabits.transformers.InputOutputMapper;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplementationTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private HabitRepository habitRepository;
    @Mock
    private PasswordHashService passwordHashService;
    @Mock
    private GenericMapperFactory mapperFactory;
    @Mock
    private GenericMapper<User, UserDTO> userMapper;
    @Mock
    private GenericMapper<Habit, HabitDTO> habitMapper;
    @Mock
    private InputOutputMapper<UserInputDTO, User, UserOutputDTO> ioMapper;

    @InjectMocks
    private UserServiceImplementation service;

    private User user;
    private UserDTO userDTO;
    private UserOutputDTO outputDTO;

    @BeforeEach
    void init() {
        user = new User();
        user.setId(10L);
        user.setEmail("john@example.com");
        userDTO = new UserDTO();
        userDTO.setId(10L);
        outputDTO = new UserOutputDTO();
        when(mapperFactory.createMapper(User.class, UserDTO.class)).thenReturn(userMapper);
        when(mapperFactory.createMapper(Habit.class, HabitDTO.class)).thenReturn(habitMapper);
        when(mapperFactory.createInputOutputMapper(UserInputDTO.class, User.class, UserOutputDTO.class))
                .thenReturn(ioMapper);
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAuthenticatedUser_returnsDtoWhenContextPresent() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("john@example.com", "pass");
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(userMapper.convertToDTO(user)).thenReturn(userDTO);

        UserDTO result = service.getAuthenticatedUser();

        assertSame(userDTO, result);
    }

    @Test
    void getAuthenticatedUser_missingAuthenticationThrows() {
        assertThrows(NoSuchElementException.class, service::getAuthenticatedUser);
    }

    @Test
    void list_usesMapper() {
        PageRequest pageable = PageRequest.of(0, 2);
        when(userRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(user), pageable, 1));
        when(userMapper.convertToDTO(user)).thenReturn(userDTO);

        Page<UserDTO> page = service.list(pageable);

        assertEquals(1, page.getTotalElements());
        assertSame(userDTO, page.getContent().getFirst());
    }

    @Test
    void create_resolvesRolesHabitsAndCoach() {
        Role existingRole = new Role();
        existingRole.setId(1L);
        existingRole.setPermission(Permission.USER_READ);
        RoleInputDTO roleExisting = new RoleInputDTO("", Permission.USER_READ);
        RoleInputDTO roleNew = new RoleInputDTO("Coach", Permission.USER_WRITE);
        Habit existingHabit = new Habit();
        existingHabit.setId(3L);
        HabitInputDTO habitExisting = new HabitInputDTO("Water", Category.FITNESS, "desc");
        HabitInputDTO habitNew = new HabitInputDTO("Meditate", Category.MINDFULNESS, "calm");
        User coach = new User();
        coach.setId(8L);
        UserInputDTO input = new UserInputDTO("John", "john@example.com", "password",
                List.of(roleExisting, roleNew), List.of(habitExisting, habitNew), 8L);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(ioMapper.convertFromInput(input)).thenReturn(user);
        when(passwordHashService.encode("password")).thenReturn("hashed");
        when(roleRepository.findByPermission(Permission.USER_READ)).thenReturn(Optional.of(existingRole));
        when(roleRepository.findByPermission(Permission.USER_WRITE)).thenReturn(Optional.empty());
        Role savedRole = new Role();
        savedRole.setId(2L);
        savedRole.setPermission(Permission.USER_WRITE);
        savedRole.setName("Coach");
        when(roleRepository.save(any(Role.class))).thenReturn(savedRole);
        when(habitRepository.findByNameAndCategory("Water", Category.FITNESS)).thenReturn(Optional.of(existingHabit));
        when(habitRepository.findByNameAndCategory("Meditate", Category.MINDFULNESS)).thenReturn(Optional.empty());
        Habit savedHabit = new Habit();
        savedHabit.setId(4L);
        savedHabit.setName("Meditate");
        when(habitRepository.save(any(Habit.class))).thenReturn(savedHabit);
        when(userRepository.findById(8L)).thenReturn(Optional.of(coach));
        when(userRepository.save(user)).thenReturn(user);
        when(ioMapper.convertToOutput(user)).thenReturn(outputDTO);

        UserOutputDTO result = service.create(input);

        assertSame(outputDTO, result);
        assertEquals("hashed", user.getPassword());
        assertTrue(user.getRoles().contains(existingRole));
        assertTrue(user.getRoles().stream().anyMatch(r -> r.getPermission() == Permission.USER_WRITE));
        assertTrue(user.getFavoriteHabits().contains(existingHabit));
        assertTrue(user.getFavoriteHabits().contains(savedHabit));
        assertEquals(coach, user.getCoach());
        verify(roleRepository).save(argThat(r -> r.getPermission() == Permission.USER_WRITE));
        verify(habitRepository).save(argThat(h -> h.getName().equals("Meditate")));
    }

    @Test
    void update_coachIdZeroClearsCoachAndEncodesPassword() {
        User existing = new User();
        existing.setId(10L);
        existing.setCoach(new User());
        UserInputDTO input = new UserInputDTO(null, "new@example.com", "newpass", null, null, 0L);
        when(userRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordHashService.encode("newpass")).thenReturn("hashed");
        when(userRepository.save(existing)).thenReturn(existing);
        when(ioMapper.convertToOutput(existing)).thenReturn(outputDTO);

        UserOutputDTO result = service.update(10L, input);

        assertSame(outputDTO, result);
        assertNull(existing.getCoach());
        assertEquals("hashed", existing.getPassword());
        assertEquals("new@example.com", existing.getEmail());
        verify(userRepository, never()).findById(0L);
    }

    @Test
    void listMyFavoriteHabits_requiresExistingUser() {
        when(userRepository.existsById(10L)).thenReturn(true);
        Habit habit = new Habit();
        when(userRepository.findFavoriteHabitsByUserId(10L, PageRequest.of(0, 1)))
                .thenReturn(new PageImpl<>(List.of(habit), PageRequest.of(0, 1), 1));
        when(habitMapper.convertToDTO(habit)).thenReturn(new HabitDTO());

        Page<HabitDTO> page = service.listMyFavoriteHabits(10L, PageRequest.of(0, 1));

        assertEquals(1, page.getTotalElements());
    }

    @Test
    void listMyFavoriteHabits_missingUserThrows() {
        when(userRepository.existsById(11L)).thenReturn(false);

        assertThrows(NoSuchElementException.class,
                () -> service.listMyFavoriteHabits(11L, PageRequest.of(0, 1)));
    }
}
