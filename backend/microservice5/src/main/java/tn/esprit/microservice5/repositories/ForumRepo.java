package tn.esprit.microservice5.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microservice5.entities.Forum;

public interface ForumRepo extends JpaRepository<Forum, Integer> {
}
