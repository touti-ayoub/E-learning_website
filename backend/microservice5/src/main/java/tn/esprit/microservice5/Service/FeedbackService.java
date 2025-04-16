package tn.esprit.microservice5.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.microservice5.DTO.FeedbackDTO;
import tn.esprit.microservice5.Model.Event;
import tn.esprit.microservice5.Model.Feedback;
import tn.esprit.microservice5.Model.User;
import tn.esprit.microservice5.Repo.IFeedbackRepository;
import tn.esprit.microservice5.Repo.IEventRepo;
import tn.esprit.microservice5.Repo.IUserRepo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    private IFeedbackRepository feedbackRepository;

    @Autowired
    private IUserRepo userRepository;

    @Autowired
    private IEventRepo eventRepository;

    //read
    public List<FeedbackDTO> getAllFeedbacks() {
        return feedbackRepository.findAll().stream()
                .map(FeedbackDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Get feedback by event ID
    public FeedbackDTO getFeedbackById(long id) {
        Optional<Feedback> feedback = feedbackRepository.findById(id);
        return feedback.map(FeedbackDTO::fromEntity).orElse(null);
    }

    // create
    public FeedbackDTO createFeedback(FeedbackDTO feedbackDTO) {
        // First fetch the user entity
        User user = userRepository.findById(feedbackDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + feedbackDTO.getUserId()));

        // Fetch the event entity using the eventId from the DTO
        Event event = eventRepository.findById(feedbackDTO.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + feedbackDTO.getEventId()));

        Feedback feedback = new Feedback();
        feedback.setUser(user);  // Set the user
        feedback.setEvent(event);  // Set the event
        feedback.setRating(feedbackDTO.getRating());
        feedback.setComments(feedbackDTO.getComments());
        feedback.setSubmittedAt(feedbackDTO.getSubmittedAt());

        feedback = feedbackRepository.save(feedback);
        return FeedbackDTO.fromEntity(feedback);
    }

    // update
    public FeedbackDTO updateFeedback(long id, FeedbackDTO feedbackDTO) {
        Optional<Feedback> feedbackOptional = feedbackRepository.findById(id);
        if (feedbackOptional.isPresent()) {
            Feedback feedback = feedbackOptional.get();

            // Fetch and set the event entity if eventId is provided
            if (feedbackDTO.getEventId() != 0) {
                Event event = eventRepository.findById(feedbackDTO.getEventId())
                        .orElseThrow(() -> new RuntimeException("Event not found with id: " + feedbackDTO.getEventId()));
                feedback.setEvent(event);
            }

            feedback.setRating(feedbackDTO.getRating());
            feedback.setComments(feedbackDTO.getComments());
            feedback.setSubmittedAt(feedbackDTO.getSubmittedAt());

            feedback = feedbackRepository.save(feedback);
            return FeedbackDTO.fromEntity(feedback);
        }
        return null;
    }

    // delete
    public void deleteFeedback(long id) {
        feedbackRepository.deleteById(id);
    }
}