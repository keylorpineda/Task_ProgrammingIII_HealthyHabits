package task.healthyhabits.configTest.dtosTest.outputs;

import org.junit.jupiter.api.Test;
import task.healthyhabits.dtos.outputs.HabitOutputDTO;
import task.healthyhabits.models.Category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class HabitOutputDTOTest {

    @Test
    void shouldSupportLifecycle() {
        HabitOutputDTO habit = new HabitOutputDTO(1L, "Hydrate", Category.DIET, "Drink water");

        assertAll(
                () -> assertThat(habit.getId()).isEqualTo(1L),
                () -> assertThat(habit.getName()).isEqualTo("Hydrate"),
                () -> assertThat(habit.getCategory()).isEqualTo(Category.DIET),
                () -> assertThat(habit.getDescription()).isEqualTo("Drink water")
        );

        HabitOutputDTO same = new HabitOutputDTO(1L, "Hydrate", Category.DIET, "Drink water");
        HabitOutputDTO different = new HabitOutputDTO(2L, "Sleep", Category.SLEEP, "Rest");

        assertAll(
                () -> assertThat(habit).isEqualTo(same),
                () -> assertThat(habit).hasSameHashCodeAs(same),
                () -> assertThat(habit).isNotEqualTo(different),
                () -> assertThat(habit.toString()).contains("Hydrate", "Drink water")
        );
    }
}

