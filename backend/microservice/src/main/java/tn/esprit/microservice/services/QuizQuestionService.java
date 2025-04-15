package tn.esprit.microservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.microservice.entities.QuizQuestion;
import tn.esprit.microservice.repositories.QuizQuestionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizQuestionService {
    private final QuizQuestionRepository quizQuestionRepository;

    public QuizQuestion createQuestion(QuizQuestion question) {
        return quizQuestionRepository.save(question);
    }

    public List<QuizQuestion> getQuestionsByQuizId(Long quizId) {
        return quizQuestionRepository.findByQuizIdQuiz(quizId);
    }
}
