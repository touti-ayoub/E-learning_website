package tn.esprit.microservice2.Rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice2.DTO.*;
import tn.esprit.microservice2.Model.*;
import tn.esprit.microservice2.comm.CourseClient;
import tn.esprit.microservice2.comm.UserClient;
import tn.esprit.microservice2.service.CourseAccessService;
import tn.esprit.microservice2.service.SubscriptionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mic2/subscription")
public class SubscriptionController {
    @Autowired
    private  CourseAccessService courseAccessService;


    @Autowired
    private UserClient userClient;
    @Autowired
    private CourseClient courseClient;

    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping("/test")
    public String test() {
        return "Subscription backend work !!!";
    }

    @PostMapping
    public ResponseEntity<SubscriptionDTO> createSubscription(@RequestBody SubCreatingRequest request) {
        try {
            SubscriptionDTO subscription = subscriptionService.createSubscription(request);
            return ResponseEntity.ok(subscription);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionDTO> getSubscription(@PathVariable Long id) {
        try {
            SubscriptionDTO subscription = subscriptionService.getSubscriptionById(id);
            return ResponseEntity.ok(subscription);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionDTO>> getAllSubscriptions() {
        List<SubscriptionDTO> subscriptions = subscriptionService.getAllSubscriptions();
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SubscriptionDTO>> getUserSubscriptions(@PathVariable Long userId) {
        try {
            List<SubscriptionDTO> subscriptions = subscriptionService.getUserSubscriptions(userId);
            return ResponseEntity.ok(subscriptions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<SubscriptionDTO> updateSubscriptionStatus(
            @PathVariable Long id,
            @RequestBody SubscriptionStatus status) {
        try {
            SubscriptionDTO subscription = subscriptionService.updateSubscriptionStatus(id, status);
            return ResponseEntity.ok(subscription);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelSubscription(@PathVariable Long id) {
        try {
            subscriptionService.cancelSubscription(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        try {
            subscriptionService.deleteSubscription(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/getUserByUN/{un}")
    public ResponseEntity<UserDTO> getSubscription(@PathVariable String un) {
        try {
            UserDTO user = subscriptionService.getUserByUsername(un);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/username/{username}")
    public ResponseEntity<UserDTO> testGetUserByUsername(@PathVariable String username) {
        try {
            UserDTO user = userClient.getUserByUsername(username);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            // Log the error
            System.err.println("Error fetching user by username: " + e.getMessage());
            // Return a more appropriate response
            return ResponseEntity.status(500).build();
        }
    }
    @GetMapping("/courses")
    public ResponseEntity<List<CourseDTO>> testGetAllCourses() {
        try {
            List<CourseDTO> courses = courseClient.getAllCourses();
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            throw e;
        }
    }


    @PostMapping("/check")
    public ResponseEntity<CourseAccessResponseDTO> checkCourseAccess(@RequestBody CourseAccessRequestDTO request) {
        try {
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


}