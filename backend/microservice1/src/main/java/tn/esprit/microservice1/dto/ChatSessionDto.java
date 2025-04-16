package tn.esprit.microservice1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionDto {
    private Long id;
    private Long userId;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ChatMessageDto> messages;
} 

