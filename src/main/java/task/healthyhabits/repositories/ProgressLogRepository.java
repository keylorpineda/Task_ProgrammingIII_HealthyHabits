package task.healthyhabits.repositories;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task.healthyhabits.models.ProgressLog;

@Repository
public interface ProgressLogRepository extends JpaRepository<ProgressLog, Long> {
    Optional<ProgressLog> findByUserIdAndDate(Long userId, LocalDate date);
    Page<ProgressLog> findAllByUserIdAndDateBetween(Long userId, LocalDate from, LocalDate to, Pageable pageable);
}
