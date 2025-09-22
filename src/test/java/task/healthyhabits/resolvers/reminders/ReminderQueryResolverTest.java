package task.healthyhabits.resolvers.reminders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
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
import task.healthyhabits.dtos.normals.ReminderDTO;
import task.healthyhabits.dtos.normals.UserDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.Frequency;
import task.healthyhabits.models.Permission;
import task.healthyhabits.models.User;
import task.healthyhabits.repositories.UserRepository;
import task.healthyhabits.resolvers.reminders.ReminderQueryResolver;
import task.healthyhabits.resolvers.reminders.ReminderQueryResolver.ReminderPage;
import task.healthyhabits.security.SecurityUtils;
import task.healthyhabits.services.reminder.ReminderService;

@ExtendWith(MockitoExtension.class)
class ReminderQueryResolverTest {

    @Mock
    private ReminderService reminderService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReminderQueryResolver resolver;

    private final UserDTO userDto = new UserDTO(3L, "Dana", "dana@example.com", "password", List.of(), List.of(), null);
    private final HabitDTO habitDto = new HabitDTO(4L, "Hydrate", Category.DIET, "Drink water");

    @AfterEach
    void cleanupSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void listRemindersReturnsPageAndRequiresPermission() {
        Pageable pageable = PageRequest.of(0, 5);
        ReminderDTO reminder = new ReminderDTO(1L, userDto, habitDto, LocalTime.of(8, 0), Frequency.DAILY);
        Page<ReminderDTO> page = new PageImpl<>(List.of(reminder), pageable, 1);
        when(reminderService.list(pageable)).thenReturn(page);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            ReminderPage result = resolver.listReminders(0, 5);

            assertThat(result.content()).containsExactly(reminder);
            assertThat(result.totalPages()).isEqualTo(page.getTotalPages());
            assertThat(result.totalElements()).isEqualTo(page.getTotalElements());
            assertThat(result.size()).isEqualTo(page.getSize());
            assertThat(result.number()).isEqualTo(page.getNumber());
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.REMINDER_READ, Permission.REMINDER_EDITOR));
        }

        verify(reminderService).list(pageable);
    }

    @Test
    void listMyRemindersUsesAuthenticatedUser() {
        setupAuthentication("dana@example.com");
        User user = new User(3L, "Dana", "dana@example.com", "password", List.of(), List.of(), null);
        when(userRepository.findByEmail("dana@example.com")).thenReturn(Optional.of(user));

        Pageable pageable = PageRequest.of(1, 2);
        ReminderDTO reminder = new ReminderDTO(2L, userDto, habitDto, LocalTime.NOON, Frequency.WEEKLY);
        Page<ReminderDTO> page = new PageImpl<>(List.of(reminder), pageable, 1);
        when(reminderService.myReminders(3L, pageable)).thenReturn(page);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            ReminderPage result = resolver.listMyReminders(1, 2);

            assertThat(result.content()).containsExactly(reminder);
            assertThat(result.totalPages()).isEqualTo(page.getTotalPages());
            assertThat(result.totalElements()).isEqualTo(page.getTotalElements());
            assertThat(result.size()).isEqualTo(page.getSize());
            assertThat(result.number()).isEqualTo(page.getNumber());
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.REMINDER_READ, Permission.REMINDER_EDITOR));
        }

        verify(userRepository).findByEmail("dana@example.com");
        verify(reminderService).myReminders(3L, pageable);
    }

    @Test
    void getReminderByIdDelegatesToService() {
        ReminderDTO reminder = new ReminderDTO(5L, userDto, habitDto, LocalTime.of(18, 30), Frequency.DAILY);
        when(reminderService.findByIdOrNull(5L)).thenReturn(reminder);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            ReminderDTO result = resolver.getReminderById(5L);

            assertThat(result).isEqualTo(reminder);
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.REMINDER_READ, Permission.REMINDER_EDITOR));
        }

        verify(reminderService).findByIdOrNull(5L);
    }

    private void setupAuthentication(String email) {
        SecurityContext context = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
    }
}
