package task.healthyhabits.resolvers.routineActivities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import task.healthyhabits.dtos.inputs.RoutineActivityInputDTO;
import task.healthyhabits.dtos.outputs.HabitOutputDTO;
import task.healthyhabits.dtos.outputs.RoutineActivityOutputDTO;
import task.healthyhabits.dtos.outputs.RoutineOutputDTO;
import task.healthyhabits.dtos.outputs.UserOutputDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.DaysOfWeek;
import task.healthyhabits.models.Permission;
import task.healthyhabits.resolvers.routineActivities.RoutineActivityMutationResolver;
import task.healthyhabits.security.SecurityUtils;
import task.healthyhabits.services.routineActivity.RoutineActivityService;

@ExtendWith(MockitoExtension.class)
class RoutineActivityMutationResolverTest {

    @Mock
    private RoutineActivityService routineActivityService;

    @InjectMocks
    private RoutineActivityMutationResolver resolver;

    @Test
    void createRoutineActivityDelegatesToService() {
        RoutineActivityInputDTO input = new RoutineActivityInputDTO(3L, 25, 40, "Core strength");
        RoutineActivityOutputDTO output = new RoutineActivityOutputDTO(11L,
                new HabitOutputDTO(3L, "Plank", Category.PHYSICAL, "Core exercise"), 25, 40, "Core strength",
                new RoutineOutputDTO(4L, "Fitness", new UserOutputDTO(7L, "Sam", "sam@example.com", List.of(), List.of(), null),
                        "Stay fit", List.of("fitness"), List.of(DaysOfWeek.MONDAY), List.of()));
        when(routineActivityService.create(4L, input)).thenReturn(output);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            RoutineActivityOutputDTO result = resolver.createRoutineActivity(4L, input);

            assertThat(result).isEqualTo(output);
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.ROUTINE_EDITOR));
        }

        verify(routineActivityService).create(4L, input);
    }

    @Test
    void updateRoutineActivityDelegatesToService() {
        RoutineActivityInputDTO input = new RoutineActivityInputDTO(5L, 15, 25, "Cool down");
        RoutineActivityOutputDTO output = new RoutineActivityOutputDTO(9L,
                new HabitOutputDTO(5L, "Yoga", Category.MENTAL, "Yoga flow"), 15, 25, "Cool down",
                new RoutineOutputDTO(8L, "Wellness", new UserOutputDTO(9L, "Taylor", "taylor@example.com", List.of(), List.of(), null),
                        "Daily wellness", List.of("balance"), List.of(DaysOfWeek.TUESDAY), List.of()));
        when(routineActivityService.update(9L, input)).thenReturn(output);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            RoutineActivityOutputDTO result = resolver.updateRoutineActivity(9L, input);

            assertThat(result).isEqualTo(output);
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.ROUTINE_EDITOR));
        }

        verify(routineActivityService).update(9L, input);
    }

    @Test
    void deleteRoutineActivityDelegatesToService() {
        when(routineActivityService.delete(12L)).thenReturn(Boolean.TRUE);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            Boolean result = resolver.deleteRoutineActivity(12L);

            assertThat(result).isTrue();
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.ROUTINE_EDITOR));
        }

        verify(routineActivityService).delete(12L);
    }
}
