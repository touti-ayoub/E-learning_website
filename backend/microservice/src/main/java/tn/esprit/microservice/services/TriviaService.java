package tn.esprit.microservice.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tn.esprit.microservice.entities.Quiz;
import tn.esprit.microservice.entities.QuizResult;
import tn.esprit.microservice.entities.QuizQuestion;
import tn.esprit.microservice.utils.TriviaCategoryMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TriviaService {

    public Quiz generateTriviaQuiz(String category, int numberOfQuestions) {
        String apiUrl = "https://opentdb.com/api.php?amount=" + numberOfQuestions;

        if (category != null && !category.isEmpty()) {
            Integer categoryId = TriviaCategoryMapper.getCategoryId(category);
            if (categoryId != null) {
                apiUrl += "&category=" + categoryId;
            } else {
                throw new IllegalArgumentException("Invalid category name: " + category);
            }
        }

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);

        if (response == null || !response.containsKey("results")) {
            throw new RuntimeException("Failed to fetch trivia questions from the API.");
        }

        Quiz quiz = new Quiz();
        quiz.setTitle("Trivia Quiz on Category: " + category);
        quiz.setDescription("Generated trivia quiz using Open Trivia Database");

        List<Map<String, Object>> questions = (List<Map<String, Object>>) response.get("results");
        for (Map<String, Object> questionData : questions) {
            QuizQuestion question = new QuizQuestion();
            question.setText((String) questionData.get("question"));
            question.setQuiz(quiz);

            List<QuizResult> answers = new ArrayList<>();
            QuizResult correctAnswer = createQuizResult((String) questionData.get("correct_answer"), true);
            correctAnswer.setQuestion(question);
            answers.add(correctAnswer);

            List<String> incorrectAnswers = (List<String>) questionData.get("incorrect_answers");
            for (String incorrect : incorrectAnswers) {
                QuizResult incorrectAnswer = createQuizResult(incorrect, false);
                incorrectAnswer.setQuestion(question);
                answers.add(incorrectAnswer);
            }

            question.setAnswers(answers);
            question.setCorrectAnswer(correctAnswer);
            quiz.getQuestions().add(question);
        }

        return quiz;
    }

    private QuizResult createQuizResult(String text, boolean isCorrect) {
        QuizResult result = new QuizResult();
        result.setText(text);
        result.setCorrect(isCorrect);
        return result;
    }
}
