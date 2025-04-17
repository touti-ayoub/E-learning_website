package tn.esprit.microservice3.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class InteractionDTO {
    private int idInteraction;
    private String contentInteraction;
    private LocalDate dateInteraction;
    private PostDTO post; // Inclure les informations du post
    private String typeInteraction; // NEW: Type of interaction (LIKE or DISLIKE)

    public void setPostId(int idPost) {
        if (this.post == null) {
            this.post = new PostDTO();
        }
        this.post.setIdPost(idPost);
    }
}