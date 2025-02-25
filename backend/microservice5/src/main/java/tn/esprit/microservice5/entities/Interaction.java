package tn.esprit.microservice5.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Interaction {
    @Id
    @GeneratedValue
    private int idInteraction;
    private String contentInteraction;
    private LocalDateTime dateInteraction;

    @ManyToOne
    private Post post;

}
