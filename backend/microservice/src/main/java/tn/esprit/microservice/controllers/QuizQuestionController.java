package tn.esprit.microservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice.entities.QuizQuestion;
import tn.esprit.microservice.services.QuizQuestionService;


@RestController
@RequestMapping("/questions")
public class QuizQuestionController {

    @Autowired
    private QuizQuestionService questionService;

    // Create a new question
    @PostMapping("/create")
    public QuizQuestion createQuestion(@RequestBody QuizQuestion question) {
        return questionService.createQuestion(question);
    }

    // Get a question by ID
    @GetMapping("/{id}")
    public QuizQuestion getQuestionById(@PathVariable Long id) {
        return questionService.getQuestionById(id);
    }
}
