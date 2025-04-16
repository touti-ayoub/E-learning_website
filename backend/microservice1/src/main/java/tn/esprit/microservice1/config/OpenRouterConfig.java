package tn.esprit.microservice1.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class OpenRouterConfig {

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Value("${openrouter.api.url:https://openrouter.ai/api/v1}")
    private String apiUrl;

    @Bean
    public WebClient openRouterWebClient() {
        log.info("Configuring OpenRouter WebClient with URL: {}", apiUrl);
        // Log partial key for debugging (avoid logging the full key)
        String maskedKey = apiKey.substring(0, 10) + "...";
        log.info("Using API key starting with: {}", maskedKey);
        
        return WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .defaultHeader("HTTP-Referer", "https://e-learning-platform.com")
                .defaultHeader("X-Title", "E-Learning Chatbot")
                .build();
    }
} 
