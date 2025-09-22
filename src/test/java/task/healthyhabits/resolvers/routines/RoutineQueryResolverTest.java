package task.healthyhabits.resolvers.routines;

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
import task.healthyhabits.dtos.normals.RoutineActivityDTO;
import task.healthyhabits.dtos.normals.RoutineDTO;
import task.healthyhabits.dtos.normals.UserDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.DaysOfWeek;
import task.healthyhabits.models.Permission;
import task.healthyhabits.models.User;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.resolvers.routines.RoutineQueryResolver;
import task.healthyhabits.resolvers.routines.RoutineQueryResolver.RoutinePage;
import task.healthyhabits.security.SecurityUtils;
import task.healthyhabits.services.routine.RoutineService;

@ExtendWith(MockitoExtension.class)
class RoutineQueryResolverTest {

    @Mock
    private RoutineService routineService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RoutineQueryResolver resolver;

    private final UserDTO userDto = new UserDTO(5L, "Alex", "alex@example.com", "password", List.of(), List.of(), null);
    private final HabitDTO habitDto = new HabitDTO(7L, "Meditate", Category.MENTAL, "Morning meditation");

    @AfterEach
    void cleanupSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void listRoutinesReturnsServicePage() {
        RoutineDTO routine = new RoutineDTO(1L, "Daily Wellness", userDto, "Stay balanced", List.of("calm"),
                List.of(DaysOfWeek.MONDAY), List.of(new RoutineActivityDTO(3L, habitDto, 15, 30, "", null)));
        Pageable pageable = PageRequest.of(0, 4);
        Page<RoutineDTO> page = new PageImpl<>(List.of(routine), pageable, 1);
        when(routineService.list(pageable)).thenReturn(page);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            RoutinePage result = resolver.listRoutines(0, 4);

            assertThat(result.content()).containsExactly(routine);
            assertThat(result.totalPages()).isEqualTo(page.getTotalPages());
            assertThat(result.totalElements()).isEqualTo(page.getTotalElements());
            assertThat(result.size()).isEqualTo(page.getSize());
            assertThat(result.number()).isEqualTo(page.getNumber());
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.ROUTINE_READ, Permission.ROUTINE_EDITOR));
        }

        verify(routineService).list(pageable);
    }

    @Test
    void listMyRoutinesUsesAuthenticatedUser() {
        setupAuthentication("alex@example.com");
        User user = new User(5L, "Alex", "alex@example.com", "password", List.of(), List.of(), null);
        when(userRepository.findByEmail("alex@example.com")).thenReturn(Optional.of(user));

        RoutineDTO routine = new RoutineDTO(2L, "Strength Plan", userDto, "Build strength", List.of("fitness"),
                List.of(DaysOfWeek.TUESDAY), List.of(new RoutineActivityDTO(4L, habitDto, 20, 40, "", null)));
        Pageable pageable = PageRequest.of(1, 2);
        Page<RoutineDTO> page = new PageImpl<>(List.of(routine), pageable, 1);
        when(routineService.myRoutines(5L, pageable)).thenReturn(page);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            RoutinePage result = resolver.listMyRoutines(1, 2);

            assertThat(result.content()).containsExactly(routine);
            assertThat(result.totalPages()).isEqualTo(page.getTotalPages());
            assertThat(result.totalElements()).isEqualTo(page.getTotalElements());
            assertThat(result.size()).isEqualTo(page.getSize());
            assertThat(result.number()).isEqualTo(page.getNumber());
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.ROUTINE_READ, Permission.ROUTINE_EDITOR));
        }

        verify(userRepository).findByEmail("alex@example.com");
        verify(routineService).myRoutines(5L, pageable);
    }

    @Test
    void listRoutinesByUserDelegatesToService() {
        RoutineDTO routine = new RoutineDTO(3L, "Flexibility", userDto, "Stretching", List.of("mobility"),
                List.of(DaysOfWeek.WEDNESDAY), List.of(new RoutineActivityDTO(6L, habitDto, 25, 45, "", null)));
        Pageable pageable = PageRequest.of(2, 3);
        Page<RoutineDTO> page = new PageImpl<>(List.of(routine), pageable, 1);
        when(routineService.byUser(5L, pageable)).thenReturn(page);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            RoutinePage result = resolver.listRoutinesByUser(5L, 2, 3);

            assertThat(result.content()).containsExactly(routine);
            assertThat(result.totalPages()).isEqualTo(page.getTotalPages());
            assertThat(result.totalElements()).isEqualTo(page.getTotalElements());
            assertThat(result.size()).isEqualTo(page.getSize());
            assertThat(result.number()).isEqualTo(page.getNumber());
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.ROUTINE_READ, Permission.ROUTINE_EDITOR));
        }

        verify(routineService).byUser(5L, pageable);
    }

    @Test
    void getRoutineByIdDelegatesToService() {
        RoutineDTO routine = new RoutineDTO(9L, "Evening Calm", userDto, "Wind down", List.of("relax"),
                List.of(DaysOfWeek.FRIDAY), List.of(new RoutineActivityDTO(8L, habitDto, 10, 20, "", null)));
        when(routineService.findByIdOrNull(9L)).thenReturn(routine);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            RoutineDTO result = resolver.getRoutineById(9L);

            assertThat(result).isEqualTo(routine);
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.ROUTINE_READ, Permission.ROUTINE_EDITOR));
        }

        verify(routineService).findByIdOrNull(9L);
    }

    private void setupAuthentication(String email) {
        SecurityContext context = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
    }
}
