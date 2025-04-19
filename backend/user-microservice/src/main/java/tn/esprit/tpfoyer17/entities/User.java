package tn.esprit.tpfoyer17.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // Example: ROLE_USER, ROLE_ADMIN

    @ElementCollection
    @CollectionTable(name = "user_quizzes", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "quiz_id")
    private List<Long> quizIds = new ArrayList<>(); // Stores IDs of quizzes taken by the user
}
