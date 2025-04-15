package tn.esprit.microservice.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
public class QuizResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idResult;

    private Long idUser;
    private int score;
    private boolean passed;
    private Date dateTaken;

    @ManyToOne
    @JoinColumn(name = "id_quiz")
    private Quiz quiz;
}
