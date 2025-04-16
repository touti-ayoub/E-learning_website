package tn.esprit.microservice5.Rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice5.DTO.FeedbackDTO;
import tn.esprit.microservice5.Service.FeedbackService;

import java.util.List;

@RestController
    @RequestMapping("/mic5/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping
    public ResponseEntity<List<FeedbackDTO>> getAllFeedbacks() {
        List<FeedbackDTO> feedbacks = feedbackService.getAllFeedbacks();
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackDTO> getFeedbackById(@PathVariable long id) {
        FeedbackDTO feedback = feedbackService.getFeedbackById(id);
        return feedback != null ? ResponseEntity.ok(feedback) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<FeedbackDTO> createFeedback(@RequestBody FeedbackDTO feedbackDTO) {
        FeedbackDTO createdFeedback = feedbackService.createFeedback(feedbackDTO);
        return ResponseEntity.ok(createdFeedback);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeedbackDTO> updateFeedback(@PathVariable long id, @RequestBody FeedbackDTO feedbackDTO) {
        FeedbackDTO updatedFeedback = feedbackService.updateFeedback(id, feedbackDTO);
        return updatedFeedback != null ? ResponseEntity.ok(updatedFeedback) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }
}