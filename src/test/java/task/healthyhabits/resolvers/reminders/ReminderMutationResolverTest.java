package task.healthyhabits.resolvers.reminders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import task.healthyhabits.dtos.inputs.ReminderInputDTO;
import task.healthyhabits.dtos.outputs.HabitOutputDTO;
import task.healthyhabits.dtos.outputs.ReminderOutputDTO;
import task.healthyhabits.dtos.outputs.UserOutputDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.Frequency;
import task.healthyhabits.models.Permission;
import task.healthyhabits.resolvers.reminders.ReminderMutationResolver;
import task.healthyhabits.security.SecurityUtils;
import task.healthyhabits.services.reminder.ReminderService;

@ExtendWith(MockitoExtension.class)
class ReminderMutationResolverTest {

    @Mock
    private ReminderService reminderService;

    @InjectMocks
    private ReminderMutationResolver resolver;

    @Test
    void createReminderDelegatesToService() {
        ReminderInputDTO input = new ReminderInputDTO(1L, 2L, LocalTime.NOON, Frequency.DAILY);
        ReminderOutputDTO output = new ReminderOutputDTO(5L, new UserOutputDTO(),
                new HabitOutputDTO(2L, "Hydrate", Category.DIET, "Drink water"), LocalTime.NOON, Frequency.DAILY);
        when(reminderService.create(input)).thenReturn(output);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            ReminderOutputDTO result = resolver.createReminder(input);
            assertThat(result).isEqualTo(output);
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.REMINDER_EDITOR));
        }

        verify(reminderService).create(input);
    }

    @Test
    void updateReminderDelegatesToService() {
        ReminderInputDTO input = new ReminderInputDTO(3L, 4L, LocalTime.MIDNIGHT, Frequency.WEEKLY);
        ReminderOutputDTO output = new ReminderOutputDTO(6L, new UserOutputDTO(),
                new HabitOutputDTO(4L, "Stretch", Category.PHYSICAL, "Stretch daily"), LocalTime.MIDNIGHT, Frequency.WEEKLY);
        when(reminderService.update(6L, input)).thenReturn(output);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            ReminderOutputDTO result = resolver.updateReminder(6L, input);
            assertThat(result).isEqualTo(output);
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.REMINDER_EDITOR));
        }

        verify(reminderService).update(6L, input);
    }

    @Test
    void deleteReminderDelegatesToService() {
        when(reminderService.delete(7L)).thenReturn(Boolean.TRUE);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            Boolean result = resolver.deleteReminder(7L);
            assertThat(result).isTrue();
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.REMINDER_EDITOR));
        }

        verify(reminderService).delete(7L);
    }
}
