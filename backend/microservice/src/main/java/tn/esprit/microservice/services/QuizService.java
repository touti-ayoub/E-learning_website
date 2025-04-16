package tn.esprit.microservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.microservice.entities.Quiz;
import tn.esprit.microservice.repositories.QuizRepository;

import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    // Create a new quiz
    public Quiz createQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    // Get all quizzes
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    // Get a quiz by ID
    public Quiz getQuizById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    }

    // Update a quiz
    public Quiz updateQuiz(Long id, Quiz quizDetails) {
        Quiz quiz = getQuizById(id);
        quiz.setTitle(quizDetails.getTitle());
        quiz.setDescription(quizDetails.getDescription());
        return quizRepository.save(quiz);
    }

    // Delete a quiz
    public void deleteQuiz(Long id) {
        quizRepository.deleteById(id);
    }

    // Associate a user with a quiz
    public void addUserToQuiz(Long quizId, Long userId) {
        Quiz quiz = getQuizById(quizId);
        if (!quiz.getUserIds().contains(userId)) {
            quiz.getUserIds().add(userId);
            quizRepository.save(quiz);
        }
    }
    public Integer getUserScore(Long quizId, Long userId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));
        return quiz.getUserScores().get(userId);
    }
    public void updateUserScore(Long quizId, Long userId, int score) {
        Optional<Quiz> optionalQuiz = quizRepository.findById(quizId);
        if (optionalQuiz.isPresent()) {
            Quiz quiz = optionalQuiz.get();
            quiz.getUserScores().put(userId, score); // Update the user's score
            quizRepository.save(quiz); // Save the updated quiz
        } else {
            throw new RuntimeException("Quiz not found");
        }
    }
}
