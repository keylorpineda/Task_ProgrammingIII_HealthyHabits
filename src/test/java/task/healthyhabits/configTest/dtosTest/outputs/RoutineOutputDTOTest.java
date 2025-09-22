package task.healthyhabits.configTest.dtosTest.outputs;

import java.util.List;
import org.junit.jupiter.api.Test;
import task.healthyhabits.dtos.outputs.HabitOutputDTO;
import task.healthyhabits.dtos.outputs.RoleOutputDTO;
import task.healthyhabits.dtos.outputs.RoutineActivityOutputDTO;
import task.healthyhabits.dtos.outputs.RoutineOutputDTO;
import task.healthyhabits.dtos.outputs.UserOutputDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.DaysOfWeek;
import task.healthyhabits.models.Permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RoutineOutputDTOTest {

    @Test
    void shouldSupportLifecycle() {
        HabitOutputDTO habit = new HabitOutputDTO(1L, "Hydrate", Category.DIET, "Drink water");
        RoleOutputDTO role = new RoleOutputDTO(2L, "Coach", Permission.ROUTINE_EDITOR);
        UserOutputDTO user = new UserOutputDTO(3L, "Alice", "alice@example.com", List.of(role), List.of(habit), 4L);
        RoutineOutputDTO routine = new RoutineOutputDTO(5L, "Morning", user, "Start strong", List.of("energy"), List.of(DaysOfWeek.MONDAY), List.of());

        assertAll(
                () -> assertThat(routine.getId()).isEqualTo(5L),
                () -> assertThat(routine.getTitle()).isEqualTo("Morning"),
                () -> assertThat(routine.getUser()).isSameAs(user),
                () -> assertThat(routine.getDescription()).isEqualTo("Start strong"),
                () -> assertThat(routine.getTags()).containsExactly("energy"),
                () -> assertThat(routine.getDaysOfWeek()).containsExactly(DaysOfWeek.MONDAY),
                () -> assertThat(routine.getActivities()).isEmpty()
        );

        RoutineOutputDTO same = new RoutineOutputDTO(5L, "Morning", user, "Start strong", List.of("energy"), List.of(DaysOfWeek.MONDAY), List.of());
        RoutineOutputDTO different = new RoutineOutputDTO(6L, "Evening", user, "Wind down", List.of("relax"), List.of(DaysOfWeek.FRIDAY), List.of());

        assertAll(
                () -> assertThat(routine).isEqualTo(same),
                () -> assertThat(routine).hasSameHashCodeAs(same),
                () -> assertThat(routine).isNotEqualTo(different),
                () -> assertThat(routine.toString()).contains("Morning", "energy")
        );
    }

    @Test
    void shouldMaintainBidirectionalActivities() {
        HabitOutputDTO habit = new HabitOutputDTO(1L, "Hydrate", Category.DIET, "Drink water");
        RoleOutputDTO role = new RoleOutputDTO(2L, "Coach", Permission.ROUTINE_EDITOR);
        UserOutputDTO user = new UserOutputDTO(3L, "Alice", "alice@example.com", List.of(role), List.of(habit), 4L);
        RoutineOutputDTO routine = new RoutineOutputDTO(5L, "Morning", user, "Start strong", List.of("energy"), List.of(DaysOfWeek.MONDAY, DaysOfWeek.TUESDAY), null);
        RoutineActivityOutputDTO activity = new RoutineActivityOutputDTO(6L, habit, 20, 30, "Notes", routine);
        routine.setActivities(List.of(activity));

        UserOutputDTO expectedUser = new UserOutputDTO(3L, "Alice", "alice@example.com", List.of(role), List.of(habit), 4L);
        HabitOutputDTO expectedHabit = new HabitOutputDTO(1L, "Hydrate", Category.DIET, "Drink water");
        RoutineOutputDTO expected = new RoutineOutputDTO(5L, "Morning", expectedUser, "Start strong", List.of("energy"), List.of(DaysOfWeek.MONDAY, DaysOfWeek.TUESDAY), null);
        RoutineActivityOutputDTO expectedActivity = new RoutineActivityOutputDTO(6L, expectedHabit, 20, 30, "Notes", expected);
        expected.setActivities(List.of(expectedActivity));

        assertAll(
                () -> assertThat(routine.getActivities()).containsExactly(activity),
                () -> assertThat(activity.getRoutine()).isSameAs(routine)
        );

        assertThat(routine)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}