package tn.esprit.microservice3.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Data
@Getter
@Setter
public class Interaction {
    @Id
    @GeneratedValue
    private int idInteraction;

    @NotBlank(message = "Content cannot be blank")
    private String contentInteraction;

    @NotNull(message = "Date cannot be null")
    private LocalDate dateInteraction;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Enumerated(EnumType.STRING)
    private InteractionType typeInteraction; // NEW: Type of interaction (LIKE or DISLIKE)
}