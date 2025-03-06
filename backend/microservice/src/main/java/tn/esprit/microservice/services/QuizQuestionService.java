package tn.esprit.microservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.microservice.entities.QuizQuestion;
import tn.esprit.microservice.repositories.QuizQuestionRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class QuizQuestionService {
    @Autowired
    private QuizQuestionRepository questionRepository;

    // Create a new question
    public QuizQuestion createQuestion(QuizQuestion question) {
        return questionRepository.save(question);
    }

    // Get all questions for a specific quiz
    public List<QuizQuestion> getQuestionsByQuizId(Long quizId) {
        return questionRepository.findByQuizId(quizId);
    }

    // Get a question by ID
    public QuizQuestion getQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
    }

    // Update a question
    public QuizQuestion updateQuestion(Long id, QuizQuestion questionDetails) {
        QuizQuestion question = getQuestionById(id);
        question.setText(questionDetails.getText());
        return questionRepository.save(question);
    }

    // Delete a question
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }
}
