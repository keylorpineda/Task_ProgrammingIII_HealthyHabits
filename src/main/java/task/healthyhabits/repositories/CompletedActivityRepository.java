package task.healthyhabits.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task.healthyhabits.models.CompletedActivity;

@Repository
public interface CompletedActivityRepository extends JpaRepository<CompletedActivity, Long> {
    List<CompletedActivity> findAllByProgressLog_User_IdAndCompletedAtBetween(Long userId, LocalDateTime start,
            LocalDateTime end);
    void deleteAllByProgressLogId(Long progressLogId);
}
