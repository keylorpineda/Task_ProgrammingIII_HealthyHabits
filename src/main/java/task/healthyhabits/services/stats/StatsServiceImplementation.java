package task.healthyhabits.services.stats;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.healthyhabits.models.Category;
import task.healthyhabits.repositories.CompletedActivityRepository;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImplementation implements StatsService {

    private static final Logger logger = LogManager.getLogger(StatsServiceImplementation.class);
    private final CompletedActivityRepository completedActivityRepository;

    @Override
    public LinkedHashMap<LocalDate, Integer> weeklyDailyCounts(Long userId, LocalDate weekStart) {
        logger.info("Calculating weekly daily counts for user {} starting {}", userId, weekStart);
        LocalDate start = weekStart;
        OffsetDateTime startDt = start.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime endDt = start.plusDays(7).atStartOfDay().atOffset(ZoneOffset.UTC);
        LinkedHashMap<LocalDate, Integer> result = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            result.put(start.plusDays(i), 0);
        }
        List<Object[]> counts = completedActivityRepository
                .countByUserIdAndCompletedAtBetweenGroupByDay(userId, startDt, endDt);
        for (Object[] row : counts) {
            LocalDate day = ((java.sql.Date) row[0]).toLocalDate();
            int count = ((Number) row[1]).intValue();
            result.put(day, count);
        }
        logger.info("Calculated weekly daily counts for user {}", userId);
        return result;
    }

    @Override
    public Map<Category, Integer> monthlyCategoryCounts(Long userId, int month, int year) {
        logger.info("Calculating monthly category counts for user {} month {} year {}", userId, month, year);
        LocalDate start = LocalDate.of(year, month, 1);
        OffsetDateTime startDt = start.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime endDt = start.plusMonths(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        Map<Category, Integer> byCategory = new EnumMap<>(Category.class);
        List<Object[]> counts = completedActivityRepository
                .countByUserIdAndCompletedAtBetweenGroupByCategory(userId, startDt, endDt);
        for (Object[] row : counts) {
            Category category = (Category) row[0];
            int count = ((Number) row[1]).intValue();
            if (category != null) {
                byCategory.put(category, count);
            }
        }
        logger.info("Calculated monthly category counts for user {}", userId);
        return byCategory;
    }
}
