package task.healthyhabits.repositories;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task.healthyhabits.models.Habit;
import task.healthyhabits.models.Category;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    Page<Habit> findAllByCategory(Category category, Pageable pageable);

    Optional<Habit> findByName(String name);
}
