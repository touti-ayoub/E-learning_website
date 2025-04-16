package tn.esprit.microservice.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
}
