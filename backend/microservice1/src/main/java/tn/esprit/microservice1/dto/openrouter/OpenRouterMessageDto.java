package tn.esprit.microservice1.dto.openrouter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenRouterMessageDto {
    private String role;
    private String content;
} 

