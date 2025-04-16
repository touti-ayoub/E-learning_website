package tn.esprit.microservice1.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tn.esprit.microservice1.dto.UserDto;

@Service
public class UserClientService {

    private final WebClient webClient;
    
    public UserClientService(@Value("${user-service.url:http://localhost:8082}") String userServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }
    
    public UserDto getUserById(Long userId) {
        return webClient.get()
                .uri("/api/users/{id}", userId)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }
    
    public boolean validateUser(Long userId) {
        try {
            UserDto user = getUserById(userId);
            return user != null;
        } catch (Exception ex) {
            return false;
        }
    }
} 