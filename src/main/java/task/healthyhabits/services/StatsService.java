package task.healthyhabits.services;

import task.healthyhabits.models.Category;
import task.healthyhabits.repositories.CompletedActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class StatsService {

    private final CompletedActivityRepository completedActivityRepository;

    public StatsService(CompletedActivityRepository completedActivityRepository) {
        this.completedActivityRepository = completedActivityRepository;
    }

    public LinkedHashMap<LocalDate, Integer> weeklyDailyCounts(Long userId, LocalDate weekStart) {
        LocalDate start = weekStart;
        LocalDate end = weekStart.plusDays(6);

        LinkedHashMap<LocalDate, Integer> result = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            result.put(start.plusDays(i), 0);
        }

        completedActivityRepository.findAll().stream()
                .filter(ca -> ca.getProgressLog() != null && ca.getProgressLog().getUser() != null)
                .filter(ca -> {
                    Long id = ca.getProgressLog().getUser().getId();
                    return id != null && id.equals(userId);
                })
                .forEach(ca -> {
                    LocalDate d = ca.getCompletedAt().toLocalDate();
                    if (!d.isBefore(start) && !d.isAfter(end)) {
                        result.computeIfPresent(d, (k, v) -> v + 1);
                    }
                });

        return result;
    }

    public Map<Category, Integer> monthlyCategoryCounts(Long userId, int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        Map<Category, Integer> byCategory = new EnumMap<>(Category.class);

        completedActivityRepository.findAll().stream()
                .filter(ca -> ca.getProgressLog() != null && ca.getProgressLog().getUser() != null)
                .filter(ca -> {
                    Long id = ca.getProgressLog().getUser().getId();
                    return id != null && id.equals(userId);
                })
                .forEach(ca -> {
                    LocalDate d = ca.getCompletedAt().toLocalDate();
                    if (!d.isBefore(start) && !d.isAfter(end) &&
                            ca.getHabit() != null && ca.getHabit().getCategory() != null) {
                        byCategory.merge(ca.getHabit().getCategory(), 1, Integer::sum);
                    }
                });

        return byCategory;
    }
}
