package task.healthyhabits.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task.healthyhabits.models.CompletedActivity;

@Repository
public interface CompletedActivityRepository extends JpaRepository<CompletedActivity, Long> {
}
