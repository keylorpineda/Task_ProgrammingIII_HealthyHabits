package task.healthyhabits.tests.resolvers.habits;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import task.healthyhabits.dtos.inputs.HabitInputDTO;
import task.healthyhabits.dtos.outputs.HabitOutputDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.Permission;
import task.healthyhabits.resolvers.habits.HabitMutationResolver;
import task.healthyhabits.security.SecurityUtils;
import task.healthyhabits.services.habit.HabitService;

@ExtendWith(MockitoExtension.class)
class HabitMutationResolverTest {

    @Mock
    private HabitService habitService;

    @InjectMocks
    private HabitMutationResolver resolver;

    @Test
    void createHabitDelegatesToService() {
        HabitInputDTO input = new HabitInputDTO("Hydrate", Category.DIET, "Drink 8 glasses of water");
        HabitOutputDTO output = new HabitOutputDTO(1L, "Hydrate", Category.DIET, "Drink 8 glasses of water");
        when(habitService.create(input)).thenReturn(output);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            HabitOutputDTO result = resolver.createHabit(input);
            assertThat(result).isEqualTo(output);
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.HABIT_EDITOR));
        }

        verify(habitService).create(input);
    }

    @Test
    void updateHabitDelegatesToService() {
        HabitInputDTO input = new HabitInputDTO("Stretch", Category.PHYSICAL, "Daily stretching");
        HabitOutputDTO output = new HabitOutputDTO(2L, "Stretch", Category.PHYSICAL, "Daily stretching");
        when(habitService.update(2L, input)).thenReturn(output);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            HabitOutputDTO result = resolver.updateHabit(2L, input);
            assertThat(result).isEqualTo(output);
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.HABIT_EDITOR));
        }

        verify(habitService).update(2L, input);
    }

    @Test
    void deleteHabitDelegatesToService() {
        when(habitService.delete(3L)).thenReturn(Boolean.TRUE);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            Boolean result = resolver.deleteHabit(3L);
            assertThat(result).isTrue();
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.HABIT_EDITOR));
        }

        verify(habitService).delete(3L);
    }
}
