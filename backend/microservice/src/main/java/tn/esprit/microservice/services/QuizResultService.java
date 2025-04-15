package tn.esprit.microservice.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.microservice.entities.QuizResult;
import tn.esprit.microservice.repositories.QuizResultRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizResultService {
    private final QuizResultRepository quizResultRepository;

    public QuizResult saveQuizResult(QuizResult quizResult) {
        return quizResultRepository.save(quizResult);
    }

    public List<QuizResult> getResultsByUserId(Long userId) {
        return quizResultRepository.findByIdUser(userId);
    }
}
