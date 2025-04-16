package tn.esprit.microservice1.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private String role; // "user" or "assistant"
    
    @Column(length = 4000)
    private String content;
    
    private LocalDateTime timestamp;
    
    @ManyToOne
    @JoinColumn(name = "session_id")
    private ChatSession chatSession;
    
    // Constructor for creating new messages
    public ChatMessage(User user, String role, String content, ChatSession chatSession) {
        this.user = user;
        this.role = role;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.chatSession = chatSession;
    }
} 

