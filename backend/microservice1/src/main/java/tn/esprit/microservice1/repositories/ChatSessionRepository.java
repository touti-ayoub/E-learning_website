package tn.esprit.microservice1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.microservice1.entities.ChatSession;
import tn.esprit.microservice1.entities.User;

import java.util.List;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    List<ChatSession> findByUserOrderByUpdatedAtDesc(User user);
} 

