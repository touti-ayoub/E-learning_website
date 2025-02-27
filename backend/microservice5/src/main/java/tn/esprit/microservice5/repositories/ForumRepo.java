package tn.esprit.microservice5.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microservice5.entities.Forum;

import java.time.LocalDateTime;
import java.util.List;

public interface ForumRepo extends JpaRepository<Forum, Integer> {
    List<Forum> findByDateCreation(LocalDateTime date);
}
