package task.healthyhabits.configTest.dtosTest.outputs;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import task.healthyhabits.dtos.outputs.CompletedActivityOutputDTO;
import task.healthyhabits.dtos.outputs.HabitOutputDTO;
import task.healthyhabits.dtos.outputs.ProgressLogOutputDTO;
import task.healthyhabits.dtos.outputs.RoleOutputDTO;
import task.healthyhabits.dtos.outputs.RoutineActivityOutputDTO;
import task.healthyhabits.dtos.outputs.RoutineOutputDTO;
import task.healthyhabits.dtos.outputs.UserOutputDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.DaysOfWeek;
import task.healthyhabits.models.Permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ProgressLogOutputDTOTest {

    @Test
    void shouldSupportLifecycle() {
        HabitOutputDTO habit = new HabitOutputDTO(1L, "Hydrate", Category.DIET, "Drink water");
        RoleOutputDTO role = new RoleOutputDTO(2L, "Coach", Permission.ROUTINE_EDITOR);
        UserOutputDTO user = new UserOutputDTO(3L, "Alice", "alice@example.com", List.of(role), List.of(habit), 4L);
        RoutineOutputDTO routine = new RoutineOutputDTO(5L, "Morning", user, "Start strong", List.of("energy"), List.of(DaysOfWeek.MONDAY), List.of());
        LocalDate date = LocalDate.of(2024, 1, 1);
        ProgressLogOutputDTO log = new ProgressLogOutputDTO(6L, user, routine, date, List.of());

        assertAll(
                () -> assertThat(log.getId()).isEqualTo(6L),
                () -> assertThat(log.getUser()).isSameAs(user),
                () -> assertThat(log.getRoutine()).isSameAs(routine),
                () -> assertThat(log.getDate()).isEqualTo(date),
                () -> assertThat(log.getCompletedActivities()).isEmpty()
        );

        ProgressLogOutputDTO same = new ProgressLogOutputDTO(6L, user, routine, date, List.of());
        ProgressLogOutputDTO different = new ProgressLogOutputDTO(7L, user, routine, date.plusDays(1), List.of());

        assertAll(
                () -> assertThat(log).isEqualTo(same),
                () -> assertThat(log).hasSameHashCodeAs(same),
                () -> assertThat(log).isNotEqualTo(different),
                () -> assertThat(log.toString()).contains("2024-01-01")
        );
    }

    @Test
    void shouldMaintainNestedActivities() {
        HabitOutputDTO habit = new HabitOutputDTO(1L, "Hydrate", Category.DIET, "Drink water");
        RoleOutputDTO role = new RoleOutputDTO(2L, "Coach", Permission.ROUTINE_EDITOR);
        UserOutputDTO user = new UserOutputDTO(3L, "Alice", "alice@example.com", List.of(role), List.of(habit), 4L);
        RoutineOutputDTO routine = new RoutineOutputDTO(5L, "Morning", user, "Start strong", List.of("energy"), List.of(DaysOfWeek.MONDAY), null);
        RoutineActivityOutputDTO activity = new RoutineActivityOutputDTO(7L, habit, 20, 30, "Notes", routine);
        routine.setActivities(List.of(activity));

        LocalDate date = LocalDate.of(2024, 1, 1);
        OffsetDateTime completedAt = OffsetDateTime.of(2024, 1, 1, 6, 30, 0, 0, ZoneOffset.UTC);
        ProgressLogOutputDTO log = new ProgressLogOutputDTO(6L, user, routine, date, null);
        CompletedActivityOutputDTO completed = new CompletedActivityOutputDTO(8L, habit, completedAt, "Great", log);
        log.setCompletedActivities(List.of(completed));

        UserOutputDTO expectedUser = new UserOutputDTO(3L, "Alice", "alice@example.com", List.of(role), List.of(habit), 4L);
        RoutineOutputDTO expectedRoutine = new RoutineOutputDTO(5L, "Morning", expectedUser, "Start strong", List.of("energy"), List.of(DaysOfWeek.MONDAY), null);
        RoutineActivityOutputDTO expectedActivity = new RoutineActivityOutputDTO(7L, habit, 20, 30, "Notes", expectedRoutine);
        expectedRoutine.setActivities(List.of(expectedActivity));
        ProgressLogOutputDTO expectedLog = new ProgressLogOutputDTO(6L, expectedUser, expectedRoutine, date, null);
        CompletedActivityOutputDTO expectedCompleted = new CompletedActivityOutputDTO(8L, habit, completedAt, "Great", expectedLog);
        expectedLog.setCompletedActivities(List.of(expectedCompleted));

        assertAll(
                () -> assertThat(log.getCompletedActivities()).containsExactly(completed),
                () -> assertThat(completed.getProgressLog()).isSameAs(log),
                () -> assertThat(log.getRoutine().getActivities()).containsExactly(activity),
                () -> assertThat(activity.getRoutine()).isSameAs(routine)
        );

        assertThat(log)
                .usingRecursiveComparison()
                .isEqualTo(expectedLog);
    }
}
