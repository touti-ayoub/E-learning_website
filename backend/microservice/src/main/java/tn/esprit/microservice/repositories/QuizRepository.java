package tn.esprit.microservice.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microservice.entities.Quiz;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
}
