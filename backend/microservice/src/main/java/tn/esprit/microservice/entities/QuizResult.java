package tn.esprit.microservice.entities;

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
    @JoinColumn(name = "question_id") // This links Answer to Question
    private QuizQuestion question; // Ensure this is the correct entity type
}
