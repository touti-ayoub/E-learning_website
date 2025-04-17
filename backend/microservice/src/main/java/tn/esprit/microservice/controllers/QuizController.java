package tn.esprit.microservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice.entities.Quiz;
import tn.esprit.microservice.services.QuizService;
import tn.esprit.microservice.services.QuizEvaluationService;
import org.springframework.web.client.RestTemplate;
import tn.esprit.microservice.services.TriviaService;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("")
public class QuizController {

    @Autowired
    private QuizService quizService;
    @Autowired
    private QuizEvaluationService quizEvaluationService;
    @Autowired
    private TriviaService triviaService;


    // Create a new quiz
    @PostMapping("/create")
    public Quiz createQuiz(@RequestBody Quiz quiz) {
        return quizService.createQuiz(quiz);
    }

    // Get all quizzes
    @GetMapping("/list")
    public List<Quiz> getAllQuizzes() {
        return quizService.getAllQuizzes();
    }

    // Get a quiz by ID
    @GetMapping("/{id}")
    public Quiz getQuizById(@PathVariable Long id) {
        return quizService.getQuizById(id);
    }

    // Evaluate a quiz and associate it with the user
    // Evaluate a quiz and associate it with the user
    @PostMapping("/{quizId}/evaluate")
    public int evaluateQuiz(
            @PathVariable Long quizId,
            @RequestBody Map<Long, Long> userAnswers,
            @RequestHeader("userId") Long userId) {

        if (userId == null) {
            throw new RuntimeException("User ID is required");
        }

        // Associate the quiz with the user
        quizService.addUserToQuiz(quizId, userId);

        // Evaluate the quiz
        int score = quizEvaluationService.evaluateQuiz(quizId, userAnswers);

        // Store the user's score
        quizService.updateUserScore(quizId, userId, score);

        return score;
    }

    // Update a quiz
    @PutMapping("/{id}/update")
    public Quiz updateQuiz(@PathVariable Long id, @RequestBody Quiz quizDetails) {
        return quizService.updateQuiz(id, quizDetails);
    }

    // Delete a quiz
    @DeleteMapping("/{id}/delete")
    public void deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
    }
    @PostMapping("/generate-trivia")
    public Quiz generateTriviaQuiz(@RequestParam String category, @RequestParam int numberOfQuestions) {
        Quiz triviaQuiz = triviaService.generateTriviaQuiz(category, numberOfQuestions);
        return quizService.createQuiz(triviaQuiz); // Save the generated quiz to the database
    }
}
