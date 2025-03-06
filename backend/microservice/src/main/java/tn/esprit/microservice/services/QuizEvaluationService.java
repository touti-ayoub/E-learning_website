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
        int score = 0;

        for (QuizQuestion question : quiz.getQuestions()) {
            Long correctAnswerId = question.getAnswers().stream()
                    .filter(QuizResult::isCorrect)
                    .map(QuizResult::getId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No correct answer found for question: " + question.getId()));

            if (userAnswers.get(question.getId()).equals(correctAnswerId)) {
                score++;
            }
        }

        return score;
    }
}
