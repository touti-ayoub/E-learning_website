package tn.esprit.microservice4.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.microservice4.entities.Challenge;
import tn.esprit.microservice4.entities.UserProgress;
import tn.esprit.microservice4.entities.User;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
}
