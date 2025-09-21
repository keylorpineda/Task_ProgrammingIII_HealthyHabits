package task.healthyhabits.repositories;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import task.healthyhabits.models.CompletedActivity;

@Repository
public interface CompletedActivityRepository extends JpaRepository<CompletedActivity, Long> {
    List<CompletedActivity> findAllByProgressLog_User_IdAndCompletedAtBetween(Long userId, OffsetDateTime start,
            OffsetDateTime end);
    void deleteAllByProgressLogId(Long progressLogId);

    @Query("SELECT DATE(c.completedAt), COUNT(c) FROM CompletedActivity c " +
            "WHERE c.progressLog.user.id = :userId AND c.completedAt >= :start AND c.completedAt < :end " +
            "GROUP BY DATE(c.completedAt)")
    List<Object[]> countByUserIdAndCompletedAtBetweenGroupByDay(Long userId, OffsetDateTime start, OffsetDateTime end);

    @Query("SELECT c.habit.category, COUNT(c) FROM CompletedActivity c " +
            "WHERE c.progressLog.user.id = :userId AND c.completedAt >= :start AND c.completedAt < :end " +
            "GROUP BY c.habit.category")
    List<Object[]> countByUserIdAndCompletedAtBetweenGroupByCategory(Long userId, OffsetDateTime start, OffsetDateTime end);
}
