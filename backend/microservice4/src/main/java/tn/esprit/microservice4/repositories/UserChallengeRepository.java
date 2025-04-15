package tn.esprit.microservice4.repositories;

import tn.esprit.microservice4.entities.Challenge;
import tn.esprit.microservice4.entities.User;
import tn.esprit.microservice4.entities.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {
    // Check if a user has already joined a challenge
    boolean existsByUserAndChallenge(User user, Challenge challenge);

    // Find completed challenges for a user
    List<UserChallenge> findByUserAndCompletedTrue(User user);

    // Find ongoing challenges for a user
    List<UserChallenge> findByUserAndCompletedFalse(User user);
}
