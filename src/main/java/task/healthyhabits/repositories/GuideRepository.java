package task.healthyhabits.repositories;
import task.healthyhabits.models.Category;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import task.healthyhabits.models.Guide;

@Repository
public interface GuideRepository extends JpaRepository<Guide, Long> {
    Page<Guide> findAllByCategory(Category category, Pageable pageable);
    Page<Guide> findAllByObjective(String objective, Pageable pageable);
    @Query(value = "SELECT DISTINCT g FROM Guide g JOIN g.recommendedFor h WHERE h.id IN :habitIds",
            countQuery = "SELECT COUNT(DISTINCT g) FROM Guide g JOIN g.recommendedFor h WHERE h.id IN :habitIds")
    Page<Guide> findAllRecommendedForHabits(@Param("habitIds") Set<Long> habitIds, Pageable pageable);
}
