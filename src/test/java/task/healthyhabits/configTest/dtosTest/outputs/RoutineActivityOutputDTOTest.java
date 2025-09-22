package task.healthyhabits.configTest.dtosTest.outputs;

import java.util.List;
import org.junit.jupiter.api.Test;
import task.healthyhabits.dtos.outputs.HabitOutputDTO;
import task.healthyhabits.dtos.outputs.RoutineActivityOutputDTO;
import task.healthyhabits.dtos.outputs.RoutineOutputDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.DaysOfWeek;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RoutineActivityOutputDTOTest {

    @Test
    void shouldRetainRoutineAssociation() {
        RoutineOutputDTO routine = new RoutineOutputDTO(1L, "Morning", null, "Start strong", List.of("energy"), List.of(DaysOfWeek.MONDAY), List.of());
        HabitOutputDTO habit = new HabitOutputDTO(2L, "Hydrate", Category.DIET, "Drink water");
        RoutineActivityOutputDTO activity = new RoutineActivityOutputDTO(3L, habit, 20, 30, "Notes", routine);

        assertAll(
                () -> assertThat(activity.getId()).isEqualTo(3L),
                () -> assertThat(activity.getHabit()).isSameAs(habit),
                () -> assertThat(activity.getDuration()).isEqualTo(20),
                () -> assertThat(activity.getTargetTime()).isEqualTo(30),
                () -> assertThat(activity.getNotes()).isEqualTo("Notes"),
                () -> assertThat(activity.getRoutine()).isSameAs(routine)
        );

        RoutineActivityOutputDTO same = new RoutineActivityOutputDTO(3L, habit, 20, 30, "Notes", routine);
        RoutineActivityOutputDTO different = new RoutineActivityOutputDTO(4L, habit, 20, 30, "Notes", routine);

        assertAll(
                () -> assertThat(activity).isEqualTo(same),
                () -> assertThat(activity).hasSameHashCodeAs(same),
                () -> assertThat(activity).isNotEqualTo(different),
                () -> assertThat(activity.toString()).contains("Notes")
        );
    }
}
