package tn.esprit.microservice.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class QuizQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idQuestion;

    private String questionText;
    private String options;
    private String correctAnswer;

    @ManyToOne
    @JoinColumn(name = "id_quiz")
    private Quiz quiz;
}
