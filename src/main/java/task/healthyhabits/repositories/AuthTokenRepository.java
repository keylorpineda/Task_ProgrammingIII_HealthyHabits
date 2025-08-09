package task.healthyhabits.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task.healthyhabits.models.AuthToken;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, String> {
}