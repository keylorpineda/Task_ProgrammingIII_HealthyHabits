package task.healthyhabits.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task.healthyhabits.models.Reminder;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
     Page<Reminder> findAllByUserId(Long userId, Pageable pageable);
}