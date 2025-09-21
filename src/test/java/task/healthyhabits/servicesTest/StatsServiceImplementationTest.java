package task.healthyhabits.servicesTest;
 
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import task.healthyhabits.models.Category;
import task.healthyhabits.repositories.CompletedActivityRepository;
import task.healthyhabits.services.stats.StatsServiceImplementation;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceImplementationTest {

    @Mock
    private CompletedActivityRepository completedActivityRepository;

    @InjectMocks
    private StatsServiceImplementation service;

    @Test
    void weeklyDailyCounts_fillsMissingDaysWithZero() {
        LocalDate start = LocalDate.of(2024, 5, 6);
        List<Object[]> aggregates = List.of(
                new Object[]{start.plusDays(1), 3L},
                new Object[]{Date.valueOf(start.plusDays(2)), 2L},
                new Object[]{start.plusDays(3).atStartOfDay().atOffset(ZoneOffset.UTC), 4L}
        );
        when(completedActivityRepository.countByUserIdAndCompletedAtBetweenGroupByDay(anyLong(), any(), any()))
                .thenReturn(aggregates);

        LinkedHashMap<LocalDate, Integer> map = service.weeklyDailyCounts(1L, start);

        assertEquals(7, map.size());
        assertIterableEquals(
                List.of(
                        start,
                        start.plusDays(1),
                        start.plusDays(2),
                        start.plusDays(3),
                        start.plusDays(4),
                        start.plusDays(5),
                        start.plusDays(6)
                ),
                map.keySet()
        );
        assertEquals(0, map.get(start));
        assertEquals(3, map.get(start.plusDays(1)));
        assertEquals(2, map.get(start.plusDays(2)));
        assertEquals(4, map.get(start.plusDays(3)));
        assertEquals(4, map.values().stream().filter(v -> v == 0).count());
        verify(completedActivityRepository)
                .countByUserIdAndCompletedAtBetweenGroupByDay(eq(1L), any(), any());
    }

    @Test
    void monthlyCategoryCounts_excludesNullCategories() {
        List<Object[]> aggregates = List.of(
                new Object[]{Category.FITNESS, 5L},
                new Object[]{null, 10L}
        );
        when(completedActivityRepository.countByUserIdAndCompletedAtBetweenGroupByCategory(anyLong(), any(), any()))
                .thenReturn(aggregates);

        Map<Category, Integer> result = service.monthlyCategoryCounts(2L, 4, 2024);

        assertEquals(1, result.size());
        assertEquals(5, result.get(Category.FITNESS));
        assertFalse(result.containsKey(null));
        verify(completedActivityRepository)
                .countByUserIdAndCompletedAtBetweenGroupByCategory(eq(2L), any(), any());
    }
}