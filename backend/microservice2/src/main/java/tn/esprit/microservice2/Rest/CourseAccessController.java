package tn.esprit.microservice2.Rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice2.DTO.CourseAccessRequestDTO;
import tn.esprit.microservice2.DTO.CourseAccessResponseDTO;
import tn.esprit.microservice2.service.CourseAccessService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/course-access")
@CrossOrigin(originPatterns = "http://localhost:*", allowCredentials = "true", exposedHeaders = "Access-Control-Allow-Origin")
public class CourseAccessController {

    private final CourseAccessService courseAccessService;

    public CourseAccessController(CourseAccessService courseAccessService) {
        this.courseAccessService = courseAccessService;
    }

    /**
     * Check if a user can access a specific course
     * @param request Contains userId and courseId
     * @return Access status with true/false
     */
    @PostMapping("/check")
    public ResponseEntity<CourseAccessResponseDTO> checkCourseAccess(@RequestBody CourseAccessRequestDTO request) {
        try {
            if (request == null || request.getUserId() == null || request.getCourseId() == null) {
                CourseAccessResponseDTO errorResponse = new CourseAccessResponseDTO();
                errorResponse.setHasAccess(false);
                errorResponse.setMessage("Invalid request parameters: Missing userId or courseId");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            boolean hasAccess = courseAccessService.hasAccessToCourse(
                    request.getUserId(), 
                    request.getCourseId()
            );
            
            CourseAccessResponseDTO response = new CourseAccessResponseDTO();
            response.setHasAccess(hasAccess);
            response.setCourseId(request.getCourseId());
            response.setUserId(request.getUserId());
            
            if (hasAccess) {
                response.setMessage("You have access to this course");
            } else {
                response.setMessage("You need to purchase this course to access its content");
            }
            
            // Always return 200 OK with the access status
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CourseAccessResponseDTO response = new CourseAccessResponseDTO();
            response.setHasAccess(false);
            response.setCourseId(request.getCourseId());
            response.setUserId(request.getUserId());
            response.setMessage("Error checking course access: " + e.getMessage());
            
            // Still return 200 OK for the frontend to handle
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * Check if a course is freely accessible (public)
     * @param courseId The ID of the course to check
     * @return Public access status
     */
    @GetMapping("/public/{courseId}")
    public ResponseEntity<Map<String, Object>> checkPublicAccess(@PathVariable Long courseId) {
        boolean isPublic = courseAccessService.isCoursePubliclyAccessible(courseId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("courseId", courseId);
        response.put("isPubliclyAccessible", isPublic);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Handle unauthorized access attempts
     * @param courseId The ID of the course
     * @return Error message
     */
    @GetMapping("/unauthorized/{courseId}")
    public ResponseEntity<Map<String, Object>> unauthorizedAccess(@PathVariable Long courseId) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Unauthorized access");
        response.put("message", "You need to purchase this course to access its content");
        response.put("courseId", courseId);
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * Simple test endpoint to verify the API is accessible
     * @return Test message
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Course access API is working properly");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Simple development test endpoint to verify the API is accessible
     * @return Test message with access granted for testing
     */
    @PostMapping("/dev-test")
    public ResponseEntity<CourseAccessResponseDTO> devTestAccess(@RequestBody CourseAccessRequestDTO request) {
        CourseAccessResponseDTO response = new CourseAccessResponseDTO();
        response.setHasAccess(true); // Always grant access for testing
        response.setCourseId(request.getCourseId());
        response.setUserId(request.getUserId());
        response.setMessage("DEV MODE: Access granted for testing purposes");
        
        return ResponseEntity.ok(response);
    }
} 