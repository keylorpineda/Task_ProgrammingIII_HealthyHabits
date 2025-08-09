package task.healthyhabits.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task.healthyhabits.models.RoutineActivity;

@Repository
public interface RoutineActivityRepository extends JpaRepository<RoutineActivity, Long> {
}
