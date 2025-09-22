package task.healthyhabits.resolvers.progress;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

import task.healthyhabits.dtos.normals.CompletedActivityDTO;
import task.healthyhabits.dtos.normals.HabitDTO;
import task.healthyhabits.dtos.normals.ProgressLogDTO;
import task.healthyhabits.dtos.normals.RoutineDTO;
import task.healthyhabits.dtos.normals.UserDTO;
import task.healthyhabits.models.Category;
import task.healthyhabits.models.DaysOfWeek;
import task.healthyhabits.models.Permission;
import task.healthyhabits.resolvers.progress.ProgressQueryResolver;
import task.healthyhabits.resolvers.progress.ProgressQueryResolver.CategoryCount;
import task.healthyhabits.resolvers.progress.ProgressQueryResolver.CompletedActivityPage;
import task.healthyhabits.resolvers.progress.ProgressQueryResolver.DailyProgress;
import task.healthyhabits.resolvers.progress.ProgressQueryResolver.MonthlyStat;
import task.healthyhabits.resolvers.progress.ProgressQueryResolver.ProgressLogPage;
import task.healthyhabits.resolvers.progress.ProgressQueryResolver.WeeklyProgress;
import task.healthyhabits.security.SecurityUtils;
import task.healthyhabits.services.completedActivity.CompletedActivityService;
import task.healthyhabits.services.progressLog.ProgressLogService;
import task.healthyhabits.services.stats.StatsService;

@ExtendWith(MockitoExtension.class)
class ProgressQueryResolverTest {

    @Mock
    private ProgressLogService progressLogService;

    @Mock
    private CompletedActivityService completedActivityService;

    @Mock
    private StatsService statsService;

    @InjectMocks
    private ProgressQueryResolver resolver;

    private final UserDTO user = new UserDTO(10L, "Alex", "alex@example.com", "password", List.of(), List.of(), null);

    private final RoutineDTO routine = new RoutineDTO(22L, "Morning", user, "Start strong", List.of("energize"),
            List.of(DaysOfWeek.MONDAY), List.of());

    @Test
    void listProgressLogsRequiresPermissionAndReturnsPage() {
        Pageable pageable = PageRequest.of(0, 3);
        ProgressLogDTO log = new ProgressLogDTO(5L, user, routine, LocalDate.of(2023, 3, 10), List.of());
        Page<ProgressLogDTO> page = new PageImpl<>(List.of(log), pageable, 1);
        when(progressLogService.list(pageable)).thenReturn(page);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            ProgressLogPage result = resolver.listProgressLogs(0, 3);

            assertThat(result.content()).containsExactly(log);
            assertThat(result.totalPages()).isEqualTo(page.getTotalPages());
            assertThat(result.totalElements()).isEqualTo(page.getTotalElements());
            assertThat(result.size()).isEqualTo(page.getSize());
            assertThat(result.number()).isEqualTo(page.getNumber());
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.PROGRESS_READ, Permission.PROGRESS_EDITOR));
        }

        verify(progressLogService).list(pageable);
    }

    @Test
    void getProgressLogByDateDelegatesToService() {
        LocalDate date = LocalDate.of(2023, 4, 1);
        ProgressLogDTO log = new ProgressLogDTO(6L, user, routine, date, List.of());
        when(progressLogService.byDate(10L, date)).thenReturn(log);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            ProgressLogDTO result = resolver.getProgressLogByDate(10L, date);

            assertThat(result).isEqualTo(log);
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.PROGRESS_READ, Permission.PROGRESS_EDITOR));
        }

        verify(progressLogService).byDate(10L, date);
    }

    @Test
    void listProgressLogsByDateRangeReturnsPagedData() {
        LocalDate from = LocalDate.of(2023, 5, 1);
        LocalDate to = LocalDate.of(2023, 5, 7);
        Pageable pageable = PageRequest.of(1, 2);
        ProgressLogDTO log = new ProgressLogDTO(7L, user, routine, LocalDate.of(2023, 5, 2), List.of());
        Page<ProgressLogDTO> page = new PageImpl<>(List.of(log), pageable, 1);
        when(progressLogService.byRange(10L, from, to, pageable)).thenReturn(page);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            ProgressLogPage result = resolver.listProgressLogsByDateRange(10L, from, to, 1, 2);

            assertThat(result.content()).containsExactly(log);
            assertThat(result.totalPages()).isEqualTo(page.getTotalPages());
            assertThat(result.totalElements()).isEqualTo(page.getTotalElements());
            assertThat(result.size()).isEqualTo(page.getSize());
            assertThat(result.number()).isEqualTo(page.getNumber());
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.PROGRESS_READ, Permission.PROGRESS_EDITOR));
        }

        verify(progressLogService).byRange(10L, from, to, pageable);
    }

    @Test
    void getProgressLogByIdRequiresPermission() {
        ProgressLogDTO log = new ProgressLogDTO(9L, user, routine, LocalDate.of(2023, 6, 3), List.of());
        when(progressLogService.findByIdOrNull(9L)).thenReturn(log);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            ProgressLogDTO result = resolver.getProgressLogById(9L);

            assertThat(result).isEqualTo(log);
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.PROGRESS_READ, Permission.PROGRESS_EDITOR));
        }

        verify(progressLogService).findByIdOrNull(9L);
    }

    @Test
    void listCompletedActivitiesReturnsPage() {
        Pageable pageable = PageRequest.of(0, 4);
        HabitDTO habit = new HabitDTO(30L, "Hydrate", Category.DIET, "Drink water");
        CompletedActivityDTO activity = new CompletedActivityDTO(40L, habit, OffsetDateTime.parse("2023-03-15T08:00:00Z"),
                "Felt great", null);
        Page<CompletedActivityDTO> page = new PageImpl<>(List.of(activity), pageable, 1);
        when(completedActivityService.list(pageable)).thenReturn(page);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            CompletedActivityPage result = resolver.listCompletedActivities(0, 4);

            assertThat(result.content()).containsExactly(activity);
            assertThat(result.totalPages()).isEqualTo(page.getTotalPages());
            assertThat(result.totalElements()).isEqualTo(page.getTotalElements());
            assertThat(result.size()).isEqualTo(page.getSize());
            assertThat(result.number()).isEqualTo(page.getNumber());
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.PROGRESS_READ, Permission.PROGRESS_EDITOR));
        }

        verify(completedActivityService).list(pageable);
    }

    @Test
    void getCompletedActivityByIdDelegatesToService() {
        HabitDTO habit = new HabitDTO(31L, "Stretch", Category.PHYSICAL, "Morning stretches");
        CompletedActivityDTO activity = new CompletedActivityDTO(41L, habit,
                OffsetDateTime.parse("2023-04-02T07:30:00Z"), "Nice session", null);
        when(completedActivityService.findByIdOrNull(41L)).thenReturn(activity);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            CompletedActivityDTO result = resolver.getCompletedActivityById(41L);

            assertThat(result).isEqualTo(activity);
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.PROGRESS_READ, Permission.PROGRESS_EDITOR));
        }

        verify(completedActivityService).findByIdOrNull(41L);
    }

    @Test
    void getWeeklyStatsBuildsDailyProgressList() {
        LocalDate weekStart = LocalDate.of(2023, 7, 3);
        Map<LocalDate, Integer> counts = new LinkedHashMap<>();
        counts.put(weekStart, 2);
        counts.put(weekStart.plusDays(1), 3);
        when(statsService.weeklyDailyCounts(10L, weekStart)).thenReturn((LinkedHashMap<LocalDate, Integer>) counts);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            WeeklyProgress result = resolver.getWeeklyStats(10L, weekStart);

            assertThat(result.weekStart()).isEqualTo(weekStart);
            assertThat(result.daily()).containsExactly(new DailyProgress(weekStart, 2),
                    new DailyProgress(weekStart.plusDays(1), 3));
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.PROGRESS_READ, Permission.PROGRESS_EDITOR));
        }

        verify(statsService).weeklyDailyCounts(10L, weekStart);
    }

    @Test
    void getMonthlyStatsTransformsCounts() {
        Map<Category, Integer> counts = new LinkedHashMap<>();
        counts.put(Category.DIET, 5);
        counts.put(Category.MENTAL, 3);
        when(statsService.monthlyCategoryCounts(10L, 8, 2023)).thenReturn(counts);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            List<MonthlyStat> result = resolver.getMonthlyStats(10L, 8, 2023);

            assertThat(result).containsExactly(
                    new MonthlyStat(8, 2023,
                            List.of(new CategoryCount(Category.DIET, 5), new CategoryCount(Category.MENTAL, 3))));
            mockedSecurity.verify(() -> SecurityUtils.requireAny(Permission.PROGRESS_READ, Permission.PROGRESS_EDITOR));
        }

        verify(statsService).monthlyCategoryCounts(10L, 8, 2023);
    }
}
