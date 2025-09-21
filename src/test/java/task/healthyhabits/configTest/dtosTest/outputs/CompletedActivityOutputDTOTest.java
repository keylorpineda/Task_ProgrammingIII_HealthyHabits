package task.healthyhabits.dtosTest.outputs;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import task.healthyhabits.dtos.outputs.CompletedActivityOutputDTO;
import task.healthyhabits.dtos.outputs.HabitOutputDTO;
import task.healthyhabits.dtos.outputs.ProgressLogOutputDTO;
import task.healthyhabits.models.Category;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CompletedActivityOutputDTOTest {

    @Test
    void shouldExposeCompletionData() {
        HabitOutputDTO habit = new HabitOutputDTO(1L, "Hydrate", Category.DIET, "Drink water");
        ProgressLogOutputDTO log = new ProgressLogOutputDTO();
        OffsetDateTime completedAt = OffsetDateTime.parse("2024-01-01T10:15:30Z");
        CompletedActivityOutputDTO activity = new CompletedActivityOutputDTO(2L, habit, completedAt, "Great", log);

        assertAll(
                () -> assertThat(activity.getId()).isEqualTo(2L),
                () -> assertThat(activity.getHabit()).isSameAs(habit),
                () -> assertThat(activity.getCompletedAt()).isEqualTo(completedAt),
                () -> assertThat(activity.getNotes()).isEqualTo("Great"),
                () -> assertThat(activity.getProgressLog()).isSameAs(log)
        );

        CompletedActivityOutputDTO same = new CompletedActivityOutputDTO(2L, habit, completedAt, "Great", log);
        CompletedActivityOutputDTO different = new CompletedActivityOutputDTO(3L, habit, completedAt, "Great", log);

        assertAll(
                () -> assertThat(activity).isEqualTo(same),
                () -> assertThat(activity).hasSameHashCodeAs(same),
                () -> assertThat(activity).isNotEqualTo(different),
                () -> assertThat(activity.toString()).contains("Great")
        );
    }
}
