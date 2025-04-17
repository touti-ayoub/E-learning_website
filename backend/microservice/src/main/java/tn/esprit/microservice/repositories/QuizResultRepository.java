package tn.esprit.microservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microservice.entities.QuizResult;

import java.util.List;

public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    List<QuizResult> findByQuestionId(Long questionId);

    // Add this method to resolve the issue
    List<QuizResult> findByQuizIdAndUserId(Long quizId, Long userId);
}
