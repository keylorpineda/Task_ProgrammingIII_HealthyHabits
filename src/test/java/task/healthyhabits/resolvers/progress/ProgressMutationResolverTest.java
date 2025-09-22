package task.healthyhabits.resolvers.progress;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import task.healthyhabits.dtos.inputs.CompletedActivityInputDTO;
import task.healthyhabits.dtos.inputs.ProgressLogInputDTO;
import task.healthyhabits.dtos.outputs.CompletedActivityOutputDTO;
import task.healthyhabits.dtos.outputs.HabitOutputDTO;
import task.healthyhabits.dtos.outputs.ProgressLogOutputDTO;
import task.healthyhabits.dtos.outputs.RoutineOutputDTO;
import task.healthyhabits.dtos.outputs.UserOutputDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.Permission;
import task.healthyhabits.resolvers.progress.ProgressMutationResolver;
import task.healthyhabits.security.SecurityUtils;
import task.healthyhabits.services.completedActivity.CompletedActivityService;
import task.healthyhabits.services.progressLog.ProgressLogService;

@ExtendWith(MockitoExtension.class)
class ProgressMutationResolverTest {

    @Mock
    private ProgressLogService progressLogService;

    @Mock
    private CompletedActivityService completedActivityService;

    @InjectMocks
    private ProgressMutationResolver resolver;

    @Test
    void createProgressLogDelegatesToService() {
        CompletedActivityInputDTO activityInput = new CompletedActivityInputDTO(4L, OffsetDateTime.now(), "Notes");
        ProgressLogInputDTO input = new ProgressLogInputDTO(1L, 2L, LocalDate.now(), List.of(activityInput));
        ProgressLogOutputDTO output = new ProgressLogOutputDTO(9L, new UserOutputDTO(),
                new RoutineOutputDTO(2L, "Routine", new UserOutputDTO(), "Desc", List.of(), List.of(), List.of()),
                input.getDate(), List.of());
        when(progressLogService.create(input)).thenReturn(output);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            ProgressLogOutputDTO result = resolver.createProgressLog(input);
            assertThat(result).isEqualTo(output);
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.PROGRESS_EDITOR));
        }

        verify(progressLogService).create(input);
    }

    @Test
    void updateProgressLogDelegatesToService() {
        CompletedActivityInputDTO activityInput = new CompletedActivityInputDTO(5L, OffsetDateTime.now(), "More notes");
        ProgressLogInputDTO input = new ProgressLogInputDTO(2L, 3L, LocalDate.now().minusDays(1), List.of(activityInput));
        ProgressLogOutputDTO output = new ProgressLogOutputDTO(10L, new UserOutputDTO(),
                new RoutineOutputDTO(3L, "Updated", new UserOutputDTO(), "Updated desc", List.of(), List.of(), List.of()),
                input.getDate(), List.of());
        when(progressLogService.update(10L, input)).thenReturn(output);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            ProgressLogOutputDTO result = resolver.updateProgressLog(10L, input);
            assertThat(result).isEqualTo(output);
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.PROGRESS_EDITOR));
        }

        verify(progressLogService).update(10L, input);
    }

    @Test
    void deleteProgressLogDelegatesToService() {
        when(progressLogService.delete(11L)).thenReturn(Boolean.TRUE);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            Boolean result = resolver.deleteProgressLog(11L);
            assertThat(result).isTrue();
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.PROGRESS_EDITOR));
        }

        verify(progressLogService).delete(11L);
    }

    @Test
    void createCompletedActivityDelegatesToService() {
        CompletedActivityInputDTO input = new CompletedActivityInputDTO(6L, OffsetDateTime.now(), "Great job");
        CompletedActivityOutputDTO output = new CompletedActivityOutputDTO(15L,
                new HabitOutputDTO(6L, "Read", Category.MENTAL, "Read a book"), input.getCompletedAt(), input.getNotes(), null);
        when(completedActivityService.create(20L, input)).thenReturn(output);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            CompletedActivityOutputDTO result = resolver.createCompletedActivity(20L, input);
            assertThat(result).isEqualTo(output);
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.PROGRESS_EDITOR));
        }

        verify(completedActivityService).create(20L, input);
    }

    @Test
    void updateCompletedActivityDelegatesToService() {
        CompletedActivityInputDTO input = new CompletedActivityInputDTO(7L, OffsetDateTime.now().minusHours(1), "Adjustments");
        CompletedActivityOutputDTO output = new CompletedActivityOutputDTO(16L,
                new HabitOutputDTO(7L, "Jog", Category.PHYSICAL, "Jog around the block"), input.getCompletedAt(), input.getNotes(), null);
        when(completedActivityService.update(16L, input)).thenReturn(output);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            CompletedActivityOutputDTO result = resolver.updateCompletedActivity(16L, input);
            assertThat(result).isEqualTo(output);
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.PROGRESS_EDITOR));
        }

        verify(completedActivityService).update(16L, input);
    }

    @Test
    void deleteCompletedActivityDelegatesToService() {
        when(completedActivityService.delete(17L)).thenReturn(Boolean.TRUE);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            Boolean result = resolver.deleteCompletedActivity(17L);
            assertThat(result).isTrue();
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.PROGRESS_EDITOR));
        }

        verify(completedActivityService).delete(17L);
    }
}
