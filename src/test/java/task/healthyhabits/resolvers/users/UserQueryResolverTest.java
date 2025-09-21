package task.healthyhabits.tests.resolvers.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import task.healthyhabits.dtos.normals.HabitDTO;
import task.healthyhabits.dtos.normals.RoleDTO;
import task.healthyhabits.dtos.normals.UserDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.Permission;
import task.healthyhabits.models.User;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.resolvers.users.UserQueryResolver;
import task.healthyhabits.resolvers.users.UserQueryResolver.HabitPage;
import task.healthyhabits.resolvers.users.UserQueryResolver.UserPage;
import task.healthyhabits.security.SecurityUtils;
import task.healthyhabits.services.user.UserService;

@ExtendWith(MockitoExtension.class)
class UserQueryResolverTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserQueryResolver resolver;

    @AfterEach
    void cleanupSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserReturnsAuthenticatedDto() {
        UserDTO expected = new UserDTO(1L, "Alice", "alice@example.com", "password", List.of(), List.of(), null);
        when(userService.getAuthenticatedUser()).thenReturn(expected);

        UserDTO result = resolver.getCurrentUser();

        assertThat(result).isEqualTo(expected);
        verify(userService).getAuthenticatedUser();
    }

    @Test
    void listUsersReturnsServicePage() {
        Pageable pageable = PageRequest.of(0, 10);
        List<UserDTO> users = List.of(new UserDTO(2L, "Bob", "bob@example.com", "secret", List.of(), List.of(), null));
        Page<UserDTO> page = new PageImpl<>(users, pageable, users.size());
        when(userService.list(pageable)).thenReturn(page);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            UserPage result = resolver.listUsers(0, 10);

            assertThat(result.content()).containsExactlyElementsOf(users);
            assertThat(result.totalPages()).isEqualTo(page.getTotalPages());
            assertThat(result.totalElements()).isEqualTo(page.getTotalElements());
            assertThat(result.size()).isEqualTo(page.getSize());
            assertThat(result.number()).isEqualTo(page.getNumber());
        }

        verify(userService).list(pageable);
    }

    @Test
    void listMyFavoriteHabitsUsesAuthenticatedUser() {
        setupAuthentication("coach@example.com");
        User coach = new User(5L, "Coach", "coach@example.com", "pass", List.of(), List.of(), null);
        when(userRepository.findByEmail("coach@example.com")).thenReturn(Optional.of(coach));

        Pageable pageable = PageRequest.of(1, 4);
        List<HabitDTO> habits = List.of(new HabitDTO(8L, "Meditate", Category.MENTAL, "Practice mindfulness"));
        Page<HabitDTO> page = new PageImpl<>(habits, pageable, habits.size());
        when(userService.listMyFavoriteHabits(5L, pageable)).thenReturn(page);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            HabitPage result = resolver.listMyFavoriteHabits(1, 4);

            assertThat(result.content()).containsExactlyElementsOf(habits);
            assertThat(result.totalPages()).isEqualTo(page.getTotalPages());
            assertThat(result.totalElements()).isEqualTo(page.getTotalElements());
            assertThat(result.size()).isEqualTo(page.getSize());
            assertThat(result.number()).isEqualTo(page.getNumber());
        }

        verify(userRepository).findByEmail("coach@example.com");
        verify(userService).listMyFavoriteHabits(5L, pageable);
    }

    @Test
    void listMyStudentsUsesAuthenticatedCoach() {
        setupAuthentication("mentor@example.com");
        User coach = new User(12L, "Mentor", "mentor@example.com", "pwd", List.of(), List.of(), null);
        when(userRepository.findByEmail("mentor@example.com")).thenReturn(Optional.of(coach));

        Pageable pageable = PageRequest.of(0, 2);
        List<UserDTO> students = List.of(
                new UserDTO(21L, "Student", "student@example.com", "pwd", List.of(new RoleDTO(1L, "Learner", Permission.USER_READ)), List.of(), 12L)
        );
        Page<UserDTO> page = new PageImpl<>(students, pageable, students.size());
        when(userService.listStudentsOfCoach(12L, pageable)).thenReturn(page);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            UserPage result = resolver.listMyStudents(0, 2);

            assertThat(result.content()).containsExactlyElementsOf(students);
            assertThat(result.totalPages()).isEqualTo(page.getTotalPages());
            assertThat(result.totalElements()).isEqualTo(page.getTotalElements());
            assertThat(result.size()).isEqualTo(page.getSize());
            assertThat(result.number()).isEqualTo(page.getNumber());
        }

        verify(userRepository).findByEmail("mentor@example.com");
        verify(userService).listStudentsOfCoach(12L, pageable);
    }

    private void setupAuthentication(String email) {
        SecurityContext context = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
    }
}
