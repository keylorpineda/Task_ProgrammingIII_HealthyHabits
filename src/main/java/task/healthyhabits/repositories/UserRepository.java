package task.healthyhabits.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import task.healthyhabits.models.User;
import task.healthyhabits.models.Habit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findAllByCoachId(Long coachId, Pageable pageable);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    
    @Query("SELECT h FROM User u JOIN u.favoriteHabits h WHERE u.id = :userId")
    Page<Habit> findFavoriteHabitsByUserId(@Param("userId") Long userId, Pageable pageable);
}
