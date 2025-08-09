package task.healthyhabits.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task.healthyhabits.models.Reminder;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
}