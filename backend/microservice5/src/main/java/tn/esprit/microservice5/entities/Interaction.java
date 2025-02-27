package tn.esprit.microservice5.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Interaction {
    @Id
    @GeneratedValue
    private int idInteraction;

    @NotBlank(message = "Content cannot be blank")
    private String contentInteraction;

    @NotNull(message = "Date cannot be null")
    private LocalDateTime dateInteraction;

    @ManyToOne
    private Post post;
}