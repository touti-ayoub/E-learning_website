package tn.esprit.microservice3.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class InteractionDTO {
    private int idInteraction;
    private String contentInteraction; // Content of the interaction (if applicable)
    private LocalDate dateInteraction; // Date of the interaction
    private int postId; // ID of the associated post
}