package tn.esprit.microservice.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice.entities.QuizResult;
import tn.esprit.microservice.services.QuizResultService;

import java.util.List;

@RestController
@RequestMapping("/results")
@RequiredArgsConstructor
public class QuizResultController {
    private final QuizResultService quizResultService;

    @PostMapping
    public QuizResult saveQuizResult(@RequestBody QuizResult quizResult) {
        return quizResultService.saveQuizResult(quizResult);
    }

    @GetMapping("/user/{userId}")
    public List<QuizResult> getResultsByUserId(@PathVariable Long userId) {
        return quizResultService.getResultsByUserId(userId);
    }
}
