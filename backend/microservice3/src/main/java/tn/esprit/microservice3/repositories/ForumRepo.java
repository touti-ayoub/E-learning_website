package tn.esprit.microservice3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microservice3.entities.Forum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ForumRepo extends JpaRepository<Forum, Integer> {
    List<Forum> findByDateCreation(LocalDate date);
}
