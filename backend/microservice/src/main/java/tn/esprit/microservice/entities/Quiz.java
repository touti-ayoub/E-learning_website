package tn.esprit.microservice.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Entity
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<QuizQuestion> questions = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_quiz", joinColumns = @JoinColumn(name = "quiz_id"))
    @Column(name = "user_id")
    private List<Long> userIds = new ArrayList<>(); // Stores user IDs

    @ElementCollection
    @CollectionTable(name = "user_quiz_scores", joinColumns = @JoinColumn(name = "quiz_id"))
    @MapKeyColumn(name = "user_id")
    @Column(name = "score")
    private Map<Long, Integer> userScores = new HashMap<>(); // Stores user scores
}
