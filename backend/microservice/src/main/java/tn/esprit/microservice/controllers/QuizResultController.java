package tn.esprit.microservice.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice.entities.QuizResult;
import tn.esprit.microservice.services.QuizResultService;

import java.util.Map;


@RestController
@RequestMapping("/answers")
public class QuizResultController {

    @Autowired
    private QuizResultService answerService;

    // Create a new answer
    @PostMapping("/create")
    public QuizResult createAnswer(@RequestBody QuizResult answer) {
        return answerService.createAnswer(answer);
    }

    // Get an answer by ID
    @GetMapping("/{id}")
    public QuizResult getAnswerById(@PathVariable Long id) {
        return answerService.getAnswerById(id);
    }
    @GetMapping("/results/{quizId}")
    public Map<String, Object> getQuizResults(
            @PathVariable Long quizId,
            @RequestHeader("userId") Long userId) {
        System.out.println("Received request for quizId: " + quizId + ", userId: " + userId);
        if (userId == null) {
            throw new RuntimeException("User ID is required");
        }
        return answerService.getQuizResults(quizId, userId);
    }
}
