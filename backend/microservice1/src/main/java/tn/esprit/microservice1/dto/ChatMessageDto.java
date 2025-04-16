package tn.esprit.microservice1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private Long id;
    private Long userId;
    private String role;
    private String content;
    private LocalDateTime timestamp;
    private Long chatSessionId;
} 

