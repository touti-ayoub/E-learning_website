package tn.esprit.microservice3.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("post")
    private List<Interaction> comments;

    private int likeCount = 0; // Ensure this is an int and initialized to 0

    private int dislikeCount = 0; // Ensure this is an int and initialized to 0

    @PrePersist
    public void prePersist() {
        this.datePost = LocalDate.now(); // Définit la date actuelle du système
    }



}