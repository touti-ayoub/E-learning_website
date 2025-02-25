package tn.esprit.microservice5.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Post {
    @Id
    @GeneratedValue
    private int idPost;
    private String content;
    private LocalDateTime datePost;

    @ManyToOne
    private Forum forum;

    @OneToMany(mappedBy = "post")
    private List<Interaction> interactions;
}