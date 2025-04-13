package tn.esprit.microservice.services;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.microservice.entities.QuizResult;
import tn.esprit.microservice.repositories.QuizResultRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class QuizResultService {
    @Autowired
    private QuizResultRepository answerRepository;

    // Create a new answer
    public QuizResult createAnswer(QuizResult answer) {
        return answerRepository.save(answer);
    }

    // Get all answers for a specific question
    public List<QuizResult> getAnswersByQuestionId(Long questionId) {
        return answerRepository.findByQuestionId(questionId);
    }

    // Get an answer by ID
    public QuizResult getAnswerById(Long id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
    }

    // Update an answer
    public QuizResult updateAnswer(Long id, QuizResult answerDetails) {
        QuizResult answer = getAnswerById(id);
        answer.setText(answerDetails.getText());
        answer.setCorrect(answerDetails.isCorrect());
        return answerRepository.save(answer);
    }

    // Delete an answer
    public void deleteAnswer(Long id) {
        answerRepository.deleteById(id);
    }
}
