package tn.esprit.microservice1.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Service for checking course access permissions by communicating with microservice2
 */
@Service
@Slf4j
public class CourseAccessClientService {

    private final WebClient webClient;

    public CourseAccessClientService(@Value("${course-service.url:http://localhost:8088/api}") String courseServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(courseServiceUrl)
                .build();
        log.info("CourseAccessClientService initialized with base URL: {}", courseServiceUrl);
    }

    /**
     * Check if a user has access to a specific course
     * @param userId User ID
     * @param courseId Course ID
     * @return true if user has access, false otherwise
     */
    public boolean checkCourseAccess(Long userId, Long courseId) {
        try {
            log.info("Checking access for user {} to course {}", userId, courseId);
            
            Map<String, Boolean> response = webClient.post()
                    .uri("/course-access/check")
                    .bodyValue(Map.of(
                            "userId", userId,
                            "courseId", courseId
                    ))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .map(result -> {
                        Boolean hasAccess = (Boolean) result.get("hasAccess");
                        log.info("Access check result for user {} to course {}: {}", 
                                userId, courseId, hasAccess);
                        return Map.of("hasAccess", hasAccess != null ? hasAccess : false);
                    })
                    .onErrorReturn(Map.of("hasAccess", false))
                    .block();
            
            return response != null && response.getOrDefault("hasAccess", false);
        } catch (Exception e) {
            log.error("Error checking course access for user {} to course {}: {}", 
                    userId, courseId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Check if a course is publicly accessible (free)
     * @param courseId Course ID
     * @return true if course is free, false if paid
     */
    public boolean isCoursePublic(Long courseId) {
        try {
            log.info("Checking if course {} is publicly accessible", courseId);
            
            Map<String, Object> response = webClient.get()
                    .uri("/course-access/public/{courseId}", courseId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            Boolean isPublic = response != null ? 
                    (Boolean) response.get("isPubliclyAccessible") : false;
            
            log.info("Course {} is publicly accessible: {}", courseId, isPublic);
            return isPublic != null && isPublic;
        } catch (Exception e) {
            log.error("Error checking if course {} is public: {}", courseId, e.getMessage(), e);
            return false;
        }
    }
} 