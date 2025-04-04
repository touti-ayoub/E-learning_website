package tn.esprit.microservice5.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.microservice5.DTO.EventDTO;
import tn.esprit.microservice5.DTO.FeedbackDTO;
import tn.esprit.microservice5.Model.Event;
import tn.esprit.microservice5.Model.Feedback;
import tn.esprit.microservice5.Model.Material;
import tn.esprit.microservice5.Model.Registration;
import tn.esprit.microservice5.Model.User;
import tn.esprit.microservice5.Repo.IEventRepo;
import tn.esprit.microservice5.Repo.IFeedbackRepository;
import tn.esprit.microservice5.Repo.IMaterialRepo;
import tn.esprit.microservice5.Repo.IRegistrationRepo;
import tn.esprit.microservice5.Repo.IUserRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private IEventRepo eventRepository;

    @Autowired
    private IRegistrationRepo registrationRepository;

    @Autowired
    private IFeedbackRepository feedbackRepository;

    @Autowired
    private IMaterialRepo materialRepository;

    @Autowired
    private IUserRepo userRepository;

    // Create
    public Event createEvent(Event event) {
        if (event.getEndTime().isBefore(event.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        return eventRepository.save(event);
    }

    // Read - Updated to return DTOs instead of entities
    public List<EventDTO> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return events.stream()
                .map(EventDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    // Get event by ID as DTO
    public EventDTO getEventDTOById(Long id) {
        return eventRepository.findById(id)
                .map(EventDTO::fromEntity)
                .orElse(null);
    }

    public List<Event> getUpcomingEvents() {
        return eventRepository.findByStartTimeAfter(LocalDateTime.now());
    }

    // Update
    public Event updateEvent(Long id, Event eventDetails) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

        event.setTitle(eventDetails.getTitle());
        event.setDescription(eventDetails.getDescription());
        event.setStartTime(eventDetails.getStartTime());
        event.setEndTime(eventDetails.getEndTime());
        event.setMaxCapacity(eventDetails.getMaxCapacity());
        event.setPlace(eventDetails.getPlace());

        if (event.getEndTime().isBefore(event.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        return eventRepository.save(event);
    }

    // Delete
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    // Methods to associate entities with an event
    public Registration addRegistrationToEvent(Long eventId, Registration registration) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        if (event.getMaxCapacity() != null &&
                event.getRegistrations().size() >= event.getMaxCapacity()) {
            throw new IllegalStateException("Event has reached maximum capacity");
        }

        registration.setEvent(event);
        Registration savedRegistration = registrationRepository.save(registration);

        event.getRegistrations().add(savedRegistration);
        eventRepository.save(event);

        return savedRegistration;
    }

    public Feedback addFeedbackToEvent(Long eventId, FeedbackDTO feedbackDTO) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        // Get User by ID (if needed)
        User user = null;
        if (feedbackDTO.getUserId() != null) {
            user = userRepository.findById(feedbackDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + feedbackDTO.getUserId()));
        }

        // Create and populate Feedback entity
        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setRating(feedbackDTO.getRating());
        feedback.setComments(feedbackDTO.getComments());
        feedback.setSubmittedAt(feedbackDTO.getSubmittedAt() != null ?
                feedbackDTO.getSubmittedAt() : LocalDateTime.now());
        feedback.setEvent(event);

        Feedback savedFeedback = feedbackRepository.save(feedback);

        event.getFeedbacks().add(savedFeedback);
        eventRepository.save(event);

        return savedFeedback;
    }

    public Material addMaterialToEvent(Long eventId, Material material) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        material.setEvent(event);
        Material savedMaterial = materialRepository.save(material);

        event.getMaterials().add(savedMaterial);
        eventRepository.save(event);

        return savedMaterial;
    }
}