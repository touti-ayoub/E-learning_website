package tn.esprit.microservice2.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String role; // Exemple : "USER" ou "ADMIN"
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Subscription> subscriptions;
// Getters et Setters
}
