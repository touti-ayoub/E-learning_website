package tn.esprit.microservice.services;



import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.microservice.entities.Quiz;
import tn.esprit.microservice.repositories.QuizRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;

    public Quiz createQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public Quiz getQuizById(Long id) {
        return quizRepository.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
    }
}
