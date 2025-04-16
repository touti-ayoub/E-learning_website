package tn.esprit.microservice1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.microservice1.entities.ChatMessage;
import tn.esprit.microservice1.entities.ChatSession;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatSessionOrderByTimestampAsc(ChatSession chatSession);
} 