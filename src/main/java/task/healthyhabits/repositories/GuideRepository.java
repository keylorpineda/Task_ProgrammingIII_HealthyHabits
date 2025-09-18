package task.healthyhabits.repositories;
import task.healthyhabits.models.Category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task.healthyhabits.models.Guide;

@Repository
public interface GuideRepository extends JpaRepository<Guide, Long> {
    Page<Guide> findAllByCategory(Category category, Pageable pageable);
    Page<Guide> findAllByObjective(String objective, Pageable pageable);
}
