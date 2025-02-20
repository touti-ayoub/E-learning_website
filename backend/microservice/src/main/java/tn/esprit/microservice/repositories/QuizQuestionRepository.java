package tn.esprit.microservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microservice.entities.QuizQuestion;

import java.util.List;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    List<QuizQuestion> findByQuizIdQuiz(Long quizId);
}
