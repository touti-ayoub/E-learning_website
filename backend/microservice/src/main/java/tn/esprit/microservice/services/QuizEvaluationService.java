package tn.esprit.microservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.microservice.entities.Quiz;
import tn.esprit.microservice.entities.QuizQuestion;
import tn.esprit.microservice.entities.QuizResult;

import java.util.Map;

@Service
public class QuizEvaluationService {

    @Autowired
    private QuizService quizService;

    // Evaluate a quiz and return the score
    public int evaluateQuiz(Long quizId, Map<Long, Long> userAnswers) {
        Quiz quiz = quizService.getQuizById(quizId);
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz not found with ID: " + quizId);
        }

        System.out.println("Evaluating quiz with ID: " + quizId);
        System.out.println("User answers: " + userAnswers);

        int score = 0;

        for (QuizQuestion question : quiz.getQuestions()) {
            Long correctAnswerId = question.getAnswers().stream()
                    .filter(QuizResult::isCorrect)
                    .map(QuizResult::getId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No correct answer found for question: " + question.getId()));

            Long userAnswerId = userAnswers.get(question.getId());
            if (userAnswerId != null && userAnswerId.equals(correctAnswerId)) {
                score++;
            }
        }

        System.out.println("Calculated score: " + score);
        return score;
    }
}
