package task.healthyhabits.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task.healthyhabits.models.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
