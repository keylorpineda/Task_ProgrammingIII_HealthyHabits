package task.healthyhabits.dtosTest.outputs;

import java.util.List;
import org.junit.jupiter.api.Test;
import task.healthyhabits.dtos.outputs.HabitOutputDTO;
import task.healthyhabits.dtos.outputs.RoleOutputDTO;
import task.healthyhabits.dtos.outputs.UserOutputDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.Permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class UserOutputDTOTest {

    @Test
    void shouldExposeUserData() {
        HabitOutputDTO habit = new HabitOutputDTO(1L, "Hydrate", Category.DIET, "Drink water");
        RoleOutputDTO role = new RoleOutputDTO(2L, "Coach", Permission.ROUTINE_EDITOR);
        UserOutputDTO user = new UserOutputDTO(3L, "Alice", "alice@example.com", List.of(role), List.of(habit), 4L);

        assertAll(
                () -> assertThat(user.getId()).isEqualTo(3L),
                () -> assertThat(user.getName()).isEqualTo("Alice"),
                () -> assertThat(user.getEmail()).isEqualTo("alice@example.com"),
                () -> assertThat(user.getRoles()).containsExactly(role),
                () -> assertThat(user.getFavoriteHabits()).containsExactly(habit),
                () -> assertThat(user.getCoachId()).isEqualTo(4L)
        );

        UserOutputDTO same = new UserOutputDTO(3L, "Alice", "alice@example.com", List.of(role), List.of(habit), 4L);
        UserOutputDTO different = new UserOutputDTO(5L, "Bob", "bob@example.com", List.of(role), List.of(habit), 4L);

        assertAll(
                () -> assertThat(user).isEqualTo(same),
                () -> assertThat(user).hasSameHashCodeAs(same),
                () -> assertThat(user).isNotEqualTo(different),
                () -> assertThat(user.toString()).contains("Alice", "alice@example.com")
        );
    }
}