package tn.esprit.microservice5.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Forum {
    @Id
    @GeneratedValue
    private int idforum;

    private String title;
    private String description;
    private LocalDateTime dateCreation;
    private int nbrPosts;

    @OneToMany(mappedBy = "forum")
    private List<Post> posts;


}
