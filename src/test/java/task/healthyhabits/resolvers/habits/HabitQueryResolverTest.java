package task.healthyhabits.resolvers.habits;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import task.healthyhabits.dtos.normals.HabitDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.resolvers.habits.HabitQueryResolver;
import task.healthyhabits.resolvers.habits.HabitQueryResolver.HabitPage;
import task.healthyhabits.security.SecurityUtils;
import task.healthyhabits.services.habit.HabitService;

@ExtendWith(MockitoExtension.class)
class HabitQueryResolverTest {

    @Mock
    private HabitService habitService;

    @InjectMocks
    private HabitQueryResolver resolver;

    @Test
    void listHabitsReturnsServicePage() {
        Pageable pageable = PageRequest.of(0, 5);
        List<HabitDTO> content = List.of(new HabitDTO(1L, "Drink Water", Category.DIET, "Stay hydrated"));
        Page<HabitDTO> page = new PageImpl<>(content, pageable, content.size());
        when(habitService.list(pageable)).thenReturn(page);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            HabitPage result = resolver.listHabits(0, 5);

            assertThat(result.content()).isEqualTo(content);
            assertThat(result.totalPages()).isEqualTo(page.getTotalPages());
            assertThat(result.totalElements()).isEqualTo(page.getTotalElements());
            assertThat(result.size()).isEqualTo(page.getSize());
            assertThat(result.number()).isEqualTo(page.getNumber());
        }

        verify(habitService).list(pageable);
    }

    @Test
    void listHabitsByCategoryReturnsFilteredPage() {
        Category category = Category.PHYSICAL;
        Pageable pageable = PageRequest.of(1, 3);
        List<HabitDTO> content = List.of(new HabitDTO(2L, "Morning Stretch", category, "Start with stretches"));
        Page<HabitDTO> page = new PageImpl<>(content, pageable, content.size());
        when(habitService.byCategory(category, pageable)).thenReturn(page);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            HabitPage result = resolver.listHabitsByCategory(category, 1, 3);

            assertThat(result.content()).containsExactlyElementsOf(content);
            assertThat(result.totalPages()).isEqualTo(page.getTotalPages());
            assertThat(result.totalElements()).isEqualTo(page.getTotalElements());
            assertThat(result.size()).isEqualTo(page.getSize());
            assertThat(result.number()).isEqualTo(page.getNumber());
        }

        verify(habitService).byCategory(category, pageable);
    }

    @Test
    void getHabitByIdDelegatesToService() {
        HabitDTO habit = new HabitDTO(3L, "Read", Category.MENTAL, "Read for 20 minutes");
        when(habitService.findByIdOrNull(3L)).thenReturn(habit);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            HabitDTO result = resolver.getHabitById(3L);
            assertThat(result).isEqualTo(habit);
        }

        verify(habitService).findByIdOrNull(3L);
    }
}
