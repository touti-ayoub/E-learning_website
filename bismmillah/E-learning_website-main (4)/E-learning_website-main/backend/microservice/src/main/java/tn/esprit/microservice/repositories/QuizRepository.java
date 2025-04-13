package tn.esprit.microservice.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microservice.entities.Quiz;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByTitleContaining(String title);
}
