package tn.esprit.microservice3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microservice3.entities.Post;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PostRepo extends JpaRepository<Post, Integer> {
    List<Post> findByDatePost(LocalDate date);
    List<Post> findByForum_IdForum(int forumId);
}