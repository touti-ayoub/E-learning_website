package tn.esprit.microservice.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class QuizResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;
    private boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "question_id") // This links QuizResult to QuizQuestion
    @JsonBackReference
    private QuizQuestion question;

    @ManyToOne
    @JoinColumn(name = "quiz_id") // This links QuizResult to Quiz
    private Quiz quiz; // Add this field to reference the Quiz entity

    private Long userId; // Add this field if it doesn't already exist
}
