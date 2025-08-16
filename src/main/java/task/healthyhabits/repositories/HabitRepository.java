package task.healthyhabits.repositories;

import java.util.Locale.Category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task.healthyhabits.models.Habit;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    Page<Habit> findAllByCategory(Category category, Pageable pageable);
}