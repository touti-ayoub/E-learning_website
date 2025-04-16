package tn.esprit.microservice3.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private Post post;
}