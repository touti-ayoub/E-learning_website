package tn.esprit.microservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice.entities.QuizQuestion;
import tn.esprit.microservice.services.QuizQuestionService;

import java.util.List;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuizQuestionController {
    private final QuizQuestionService quizQuestionService;

    @PostMapping
    public QuizQuestion createQuestion(@RequestBody QuizQuestion question) {
        return quizQuestionService.createQuestion(question);
    }

    @GetMapping("/quiz/{quizId}")
    public List<QuizQuestion> getQuestionsByQuizId(@PathVariable Long quizId) {
        return quizQuestionService.getQuestionsByQuizId(quizId);
    }
}
