package tn.esprit.microservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.microservice.entities.Quiz;
import tn.esprit.microservice.entities.QuizQuestion;
import tn.esprit.microservice.entities.QuizResult;
import tn.esprit.microservice.repositories.QuizRepository;
import tn.esprit.microservice.repositories.QuizResultRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuizResultService {

    @Autowired
    private QuizResultRepository quizResultRepository;

    @Autowired
    private QuizRepository quizRepository;

    // Create a new answer
    public QuizResult createAnswer(QuizResult answer) {
        return quizResultRepository.save(answer);
    }

    // Get all answers for a specific question
    public List<QuizResult> getAnswersByQuestionId(Long questionId) {
        return quizResultRepository.findByQuestionId(questionId);
    }

    // Get an answer by ID
    public QuizResult getAnswerById(Long id) {
        return quizResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
    }

    // Update an answer
    public QuizResult updateAnswer(Long id, QuizResult answerDetails) {
        QuizResult answer = getAnswerById(id);
        answer.setText(answerDetails.getText());
        answer.setCorrect(answerDetails.isCorrect());
        return quizResultRepository.save(answer);
    }

    // Delete an answer
    public void deleteAnswer(Long id) {
        quizResultRepository.deleteById(id);
    }

    // Get quiz results for a specific user and quiz
    public Map<String, Object> getQuizResults(Long quizId, Long userId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        List<QuizResult> userAnswers = quizResultRepository.findByQuizIdAndUserId(quizId, userId);

        Map<String, Object> result = new HashMap<>();
        result.put("quizTitle", quiz.getTitle());
        result.put("questions", quiz.getQuestions().stream().map(question -> {
            Map<String, Object> questionDetails = new HashMap<>();
            questionDetails.put("questionText", question.getText());

            QuizResult userAnswer = userAnswers.stream()
                    .filter(answer -> answer.getQuestion().getId().equals(question.getId()))
                    .findFirst()
                    .orElse(null);

            questionDetails.put("userAnswer", userAnswer != null ? userAnswer.getText() : "No Answer");
            questionDetails.put("correctAnswer", question.getCorrectAnswer().getText());
            questionDetails.put("isCorrect", userAnswer != null && userAnswer.isCorrect());

            return questionDetails;
        }).toList());

        return result;
    }
}
