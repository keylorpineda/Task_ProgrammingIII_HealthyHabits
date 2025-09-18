package task.healthyhabits.services.stats;

import task.healthyhabits.models.Category;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public interface StatsService {
    LinkedHashMap<LocalDate, Integer> weeklyDailyCounts(Long userId, LocalDate weekStart);
    Map<Category, Integer> monthlyCategoryCounts(Long userId, int month, int year);
}
