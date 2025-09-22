package task.healthyhabits.resolvers.routines;

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
import task.healthyhabits.dtos.inputs.RoutineInputDTO;
import task.healthyhabits.dtos.outputs.HabitOutputDTO;
import task.healthyhabits.dtos.outputs.RoutineActivityOutputDTO;
import task.healthyhabits.dtos.outputs.RoutineOutputDTO;
import task.healthyhabits.dtos.outputs.UserOutputDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.DaysOfWeek;
import task.healthyhabits.models.Permission;
import task.healthyhabits.resolvers.routines.RoutineMutationResolver;
import task.healthyhabits.security.SecurityUtils;
import task.healthyhabits.services.routine.RoutineService;

@ExtendWith(MockitoExtension.class)
class RoutineMutationResolverTest {

    @Mock
    private RoutineService routineService;

    @InjectMocks
    private RoutineMutationResolver resolver;

    @Test
    void createRoutineDelegatesToService() {
        RoutineActivityInputDTO activityInput = new RoutineActivityInputDTO(9L, 30, 45, "Focus");
        RoutineInputDTO input = new RoutineInputDTO("Mindful Start", "Begin with mindfulness", List.of("calm"),
                List.of(DaysOfWeek.MONDAY), 5L, List.of(activityInput));
        RoutineActivityOutputDTO activityOutput = new RoutineActivityOutputDTO(2L,
                new HabitOutputDTO(9L, "Breathing", Category.MENTAL, "Deep breathing"), 30, 45, "Focus", null);
        RoutineOutputDTO output = new RoutineOutputDTO(3L, "Mindful Start",
                new UserOutputDTO(5L, "Alex", "alex@example.com", List.of(), List.of(), null),
                "Begin with mindfulness", List.of("calm"), List.of(DaysOfWeek.MONDAY), List.of(activityOutput));
        when(routineService.create(input)).thenReturn(output);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            RoutineOutputDTO result = resolver.createRoutine(input);

            assertThat(result).isEqualTo(output);
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.ROUTINE_EDITOR));
        }

        verify(routineService).create(input);
    }

    @Test
    void updateRoutineDelegatesToService() {
        RoutineActivityInputDTO activityInput = new RoutineActivityInputDTO(7L, 20, 30, "Evening");
        RoutineInputDTO input = new RoutineInputDTO("Evening Relax", "Relax before bed", List.of("relax"),
                List.of(DaysOfWeek.FRIDAY), 8L, List.of(activityInput));
        RoutineActivityOutputDTO activityOutput = new RoutineActivityOutputDTO(4L,
                new HabitOutputDTO(7L, "Stretch", Category.PHYSICAL, "Light stretches"), 20, 30, "Evening", null);
        RoutineOutputDTO output = new RoutineOutputDTO(6L, "Evening Relax",
                new UserOutputDTO(8L, "Jamie", "jamie@example.com", List.of(), List.of(), null),
                "Relax before bed", List.of("relax"), List.of(DaysOfWeek.FRIDAY), List.of(activityOutput));
        when(routineService.update(6L, input)).thenReturn(output);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            RoutineOutputDTO result = resolver.updateRoutine(6L, input);

            assertThat(result).isEqualTo(output);
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.ROUTINE_EDITOR));
        }

        verify(routineService).update(6L, input);
    }

    @Test
    void deleteRoutineDelegatesToService() {
        when(routineService.delete(10L)).thenReturn(Boolean.TRUE);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            Boolean result = resolver.deleteRoutine(10L);

            assertThat(result).isTrue();
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.ROUTINE_EDITOR));
        }

        verify(routineService).delete(10L);
    }
}
