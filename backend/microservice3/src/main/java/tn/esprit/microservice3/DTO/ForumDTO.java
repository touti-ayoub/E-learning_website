package tn.esprit.microservice3.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ForumDTO {
    private int idForum;
    private String title;
    private String description;
    private LocalDate dateCreation;
    private int nbrPosts;
}