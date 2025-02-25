package tn.esprit.microservice5.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microservice5.entities.Post;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepo extends JpaRepository<Post, Integer> {
    List<Post> findByDatePost(LocalDateTime date);
}
