package tn.esprit.microservice1.dto.openrouter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenRouterResponseDto {
    private String id;
    private List<ChoiceDto> choices;
    private String model;
    private UsageDto usage;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChoiceDto {
        private MessageDto message;
        private Integer index;
        private String finish_reason;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageDto {
        private String role;
        private String content;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsageDto {
        private Integer prompt_tokens;
        private Integer completion_tokens;
        private Integer total_tokens;
    }
} 