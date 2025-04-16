package tn.esprit.microservice1.dto.openrouter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenRouterRequestDto {
    private String model;
    private List<OpenRouterMessageDto> messages;
    private Double temperature;
    private Integer max_tokens;
    private Boolean stream;
    
    // Default model configuration for our chatbot
    public static OpenRouterRequestDto createDefault(List<OpenRouterMessageDto> messages) {
        OpenRouterRequestDto request = new OpenRouterRequestDto();
        request.setModel("mistralai/mistral-7b-instruct"); // A good free model
        request.setMessages(messages);
        request.setTemperature(0.7);
        request.setMax_tokens(1000);
        request.setStream(false);
        return request;
    }
} 