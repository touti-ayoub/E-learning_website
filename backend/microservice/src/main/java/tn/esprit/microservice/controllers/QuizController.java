package tn.esprit.microservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice.entities.Quiz;
import tn.esprit.microservice.services.QuizService;
import tn.esprit.microservice.services.QuizEvaluationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;
    @Autowired
    private QuizEvaluationService quizEvaluationService;

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

    // Evaluate a quiz
    @PostMapping("/{quizId}/evaluate")
    public int evaluateQuiz(@PathVariable Long quizId, @RequestBody Map<Long, Long> userAnswers) {
        return quizEvaluationService.evaluateQuiz(quizId, userAnswers);
    }
}
