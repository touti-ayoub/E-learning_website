package tn.esprit.microservice.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice.entities.QuizResult;
import tn.esprit.microservice.services.QuizResultService;


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
}
