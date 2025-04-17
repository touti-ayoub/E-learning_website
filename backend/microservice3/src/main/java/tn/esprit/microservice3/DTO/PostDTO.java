package tn.esprit.microservice3.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PostDTO {
    private int idPost;
    private String content;
    private LocalDate datePost;
    private int forumId; // ID of the associated forum

}