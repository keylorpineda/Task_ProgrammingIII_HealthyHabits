package task.healthyhabits.configTest.dtosTest.outputs;

import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import task.healthyhabits.dtos.outputs.HabitOutputDTO;
import task.healthyhabits.dtos.outputs.ReminderOutputDTO;
import task.healthyhabits.dtos.outputs.RoleOutputDTO;
import task.healthyhabits.dtos.outputs.UserOutputDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.Frequency;
import task.healthyhabits.models.Permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ReminderOutputDTOTest {

    @Test
    void shouldExposeReminderInformation() {
        HabitOutputDTO habit = new HabitOutputDTO(1L, "Hydrate", Category.DIET, "Drink water");
        RoleOutputDTO role = new RoleOutputDTO(2L, "Coach", Permission.ROUTINE_EDITOR);
        UserOutputDTO user = new UserOutputDTO(3L, "Alice", "alice@example.com", List.of(role), List.of(habit), 4L);
        ReminderOutputDTO reminder = new ReminderOutputDTO(5L, user, habit, LocalTime.NOON, Frequency.DAILY);

        assertAll(
                () -> assertThat(reminder.getId()).isEqualTo(5L),
                () -> assertThat(reminder.getUser()).isSameAs(user),
                () -> assertThat(reminder.getHabit()).isSameAs(habit),
                () -> assertThat(reminder.getTime()).isEqualTo(LocalTime.NOON),
                () -> assertThat(reminder.getFrequency()).isEqualTo(Frequency.DAILY)
        );

        ReminderOutputDTO same = new ReminderOutputDTO(5L, user, habit, LocalTime.NOON, Frequency.DAILY);
        ReminderOutputDTO different = new ReminderOutputDTO(6L, user, habit, LocalTime.MIDNIGHT, Frequency.WEEKLY);

        assertAll(
                () -> assertThat(reminder).isEqualTo(same),
                () -> assertThat(reminder).hasSameHashCodeAs(same),
                () -> assertThat(reminder).isNotEqualTo(different),
                () -> assertThat(reminder.toString()).contains("ReminderOutputDTO", "NOON")
        );

        UserOutputDTO expectedUser = new UserOutputDTO(3L, "Alice", "alice@example.com", List.of(role), List.of(habit), 4L);
        HabitOutputDTO expectedHabit = new HabitOutputDTO(1L, "Hydrate", Category.DIET, "Drink water");
        ReminderOutputDTO expected = new ReminderOutputDTO(5L, expectedUser, expectedHabit, LocalTime.NOON, Frequency.DAILY);

        assertThat(reminder)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
