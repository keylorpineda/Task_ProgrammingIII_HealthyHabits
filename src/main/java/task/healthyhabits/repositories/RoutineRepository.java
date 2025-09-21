package task.healthyhabits.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task.healthyhabits.models.Routine;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {
    Page<Routine> findAllByUserId(Long userId, Pageable pageable);
}
