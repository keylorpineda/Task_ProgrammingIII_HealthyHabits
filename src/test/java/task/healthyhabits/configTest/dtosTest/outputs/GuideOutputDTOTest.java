package task.healthyhabits.configTest.dtosTest.outputs;
import java.util.List;
import org.junit.jupiter.api.Test;
import task.healthyhabits.dtos.outputs.GuideOutputDTO;
import task.healthyhabits.dtos.outputs.HabitOutputDTO;
import task.healthyhabits.models.Category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class GuideOutputDTOTest {

    @Test
    void shouldExposeRecommendedHabits() {
        HabitOutputDTO hydration = new HabitOutputDTO(1L, "Hydration", Category.DIET, "Drink water");
        HabitOutputDTO stretching = new HabitOutputDTO(2L, "Stretch", Category.PHYSICAL, "Stretch daily");
        GuideOutputDTO guide = new GuideOutputDTO(3L, "Morning", "Start fresh", Category.MENTAL, "Feel better", List.of(hydration, stretching));

        assertAll(
                () -> assertThat(guide.getId()).isEqualTo(3L),
                () -> assertThat(guide.getTitle()).isEqualTo("Morning"),
                () -> assertThat(guide.getContent()).isEqualTo("Start fresh"),
                () -> assertThat(guide.getCategory()).isEqualTo(Category.MENTAL),
                () -> assertThat(guide.getObjective()).isEqualTo("Feel better"),
                () -> assertThat(guide.getRecommendedFor()).containsExactly(hydration, stretching)
        );

        GuideOutputDTO same = new GuideOutputDTO(3L, "Morning", "Start fresh", Category.MENTAL, "Feel better", List.of(hydration, stretching));
        GuideOutputDTO different = new GuideOutputDTO(4L, "Evening", "Relax", Category.SLEEP, "Rest", List.of(stretching));

        assertAll(
                () -> assertThat(guide).isEqualTo(same),
                () -> assertThat(guide).hasSameHashCodeAs(same),
                () -> assertThat(guide).isNotEqualTo(different),
                () -> assertThat(guide.toString()).contains("Morning", "Feel better")
        );
    }
}