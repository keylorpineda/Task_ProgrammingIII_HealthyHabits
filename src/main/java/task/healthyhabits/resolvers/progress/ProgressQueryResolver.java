package task.healthyhabits.resolvers.progress;

import lombok.RequiredArgsConstructor;
import task.healthyhabits.dtos.pages.PageDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import task.healthyhabits.dtos.normals.CompletedActivityDTO;
import task.healthyhabits.dtos.normals.ProgressLogDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.Permission;
import task.healthyhabits.services.completedActivity.CompletedActivityService;
import task.healthyhabits.services.progressLog.ProgressLogService;
import task.healthyhabits.services.stats.StatsService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static task.healthyhabits.security.SecurityUtils.requireAny;

@Controller
@RequiredArgsConstructor
public class ProgressQueryResolver {

    private final ProgressLogService progressLogService;
    private final CompletedActivityService completedActivityService;
    private final StatsService statsService;

    @QueryMapping
    public ProgressLogPage listProgressLogs(@Argument int page, @Argument int size) {
        requireAny(Permission.PROGRESS_READ, Permission.PROGRESS_EDITOR);
        Pageable pageable = PageRequest.of(page, size);
        PageDTO<ProgressLogDTO> pageDto = PageDTO.from(progressLogService.list(pageable));
        return ProgressLogPage.from(pageDto);
    }

    @QueryMapping
    public ProgressLogDTO getProgressLogByDate(@Argument Long userId, @Argument LocalDate date) {
        requireAny(Permission.PROGRESS_READ, Permission.PROGRESS_EDITOR);
        return progressLogService.byDate(userId, date);
    }

    @QueryMapping
    public ProgressLogPage listProgressLogsByDateRange(@Argument Long userId,
            @Argument LocalDate from,
            @Argument LocalDate to,
            @Argument int page,
            @Argument int size) {
        requireAny(Permission.PROGRESS_READ, Permission.PROGRESS_EDITOR);
        Pageable pageable = PageRequest.of(page, size);
        PageDTO<ProgressLogDTO> pageDto = PageDTO.from(progressLogService.byRange(userId, from, to, pageable));
        return ProgressLogPage.from(pageDto);
    }

    @QueryMapping
    public ProgressLogDTO getProgressLogById(@Argument Long id) {
        requireAny(Permission.PROGRESS_READ, Permission.PROGRESS_EDITOR);
        return progressLogService.findByIdOrNull(id);
    }

    @QueryMapping
    public CompletedActivityPage listCompletedActivities(@Argument int page, @Argument int size) {
        requireAny(Permission.PROGRESS_READ, Permission.PROGRESS_EDITOR);
        Pageable pageable = PageRequest.of(page, size);
        PageDTO<CompletedActivityDTO> pageDto = PageDTO.from(completedActivityService.list(pageable));
        return CompletedActivityPage.from(pageDto);
    }

    @QueryMapping
    public CompletedActivityDTO getCompletedActivityById(@Argument Long id) {
        requireAny(Permission.PROGRESS_READ, Permission.PROGRESS_EDITOR);
        return completedActivityService.findByIdOrNull(id);
    }

    @QueryMapping
    public WeeklyProgress getWeeklyStats(@Argument Long userId, @Argument LocalDate weekStart) {
        requireAny(Permission.PROGRESS_READ, Permission.PROGRESS_EDITOR);
        Map<LocalDate, Integer> map = statsService.weeklyDailyCounts(userId, weekStart);
        List<DailyProgress> daily = new ArrayList<>();
        for (Map.Entry<LocalDate, Integer> e : map.entrySet()) {
            daily.add(new DailyProgress(e.getKey(), e.getValue()));
        }
        return new WeeklyProgress(weekStart, daily);
    }

    @QueryMapping
    public List<MonthlyStat> getMonthlyStats(@Argument Long userId, @Argument int month, @Argument int year) {
        requireAny(Permission.PROGRESS_READ, Permission.PROGRESS_EDITOR);
        Map<Category, Integer> byCat = statsService.monthlyCategoryCounts(userId, month, year);
        List<CategoryCount> list = new ArrayList<>();
        for (Map.Entry<Category, Integer> e : byCat.entrySet()) {
            list.add(new CategoryCount(e.getKey(), e.getValue()));
        }
        return List.of(new MonthlyStat(month, year, list));
    }

    public record ProgressLogPage(
            List<ProgressLogDTO> content,
            int totalPages,
            long totalElements,
            int size,
            int number,
            boolean hasNext,
            boolean hasPrevious) {
        public static ProgressLogPage from(PageDTO<ProgressLogDTO> dto) {
            return new ProgressLogPage(
                    dto.content(),
                    dto.totalPages(),
                    dto.totalElements(),
                    dto.size(),
                    dto.number(),
                    dto.hasNext(),
                    dto.hasPrevious());
        }
    }

    public record CompletedActivityPage(
            List<CompletedActivityDTO> content,
            int totalPages,
            long totalElements,
            int size,
            int number,
            boolean hasNext,
            boolean hasPrevious) {
        public static CompletedActivityPage from(PageDTO<CompletedActivityDTO> dto) {
            return new CompletedActivityPage(
                    dto.content(),
                    dto.totalPages(),
                    dto.totalElements(),
                    dto.size(),
                    dto.number(),
                    dto.hasNext(),
                    dto.hasPrevious());
        }
    }

    public record CategoryCount(Category category, int count) {
    }

    public record MonthlyStat(int month, int year, List<CategoryCount> byCategory) {
    }

    public record DailyProgress(LocalDate date, int completedCount) {
    }

    public record WeeklyProgress(LocalDate weekStart, List<DailyProgress> daily) {
    }
}
