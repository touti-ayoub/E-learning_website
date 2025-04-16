package tn.esprit.microservice3.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Post {
    @Id
    @GeneratedValue
    private int idPost;
    @NotBlank(message = "Content cannot be blank")
    private String content;

    @NotNull(message = "Date cannot be null")
    private LocalDate datePost;

    @ManyToOne
    private Forum forum;

    @OneToMany(mappedBy = "post")
    private List<Interaction> interactions;
    @PrePersist
    public void prePersist() {
        this.datePost = LocalDate.now(); // Définit la date actuelle du système
    }
}