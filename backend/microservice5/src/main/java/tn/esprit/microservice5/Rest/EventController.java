package tn.esprit.microservice5.Rest;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice5.DTO.EventDTO;
import tn.esprit.microservice5.DTO.FeedbackDTO;
import tn.esprit.microservice5.Model.Event;
import tn.esprit.microservice5.Model.Feedback;
import tn.esprit.microservice5.Model.Material;
import tn.esprit.microservice5.Model.Registration;
import tn.esprit.microservice5.Service.EventService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/mic5/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody Event event) {
        try {
            Event createdEvent = eventService.createEvent(event);
            return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<EventDTO> eventDTOs = eventService.getAllEvents();
        return new ResponseEntity<>(eventDTOs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Optional<Event> event = eventService.getEventById(id);
        return event
                .map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Event>> getUpcomingEvents() {
        List<Event> events = eventService.getUpcomingEvents();
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @Valid @RequestBody Event event) {
        try {
            Event updatedEvent = eventService.updateEvent(id, event);
            return new ResponseEntity<>(updatedEvent, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        try {
            eventService.deleteEvent(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Add a registration to an event
     */
    @PostMapping("/{id}/registrations")
    public ResponseEntity<?> addRegistrationToEvent(@PathVariable("id") Long eventId,
                                                    @Valid @RequestBody Registration registration) {
        try {
            Registration savedRegistration = eventService.addRegistrationToEvent(eventId, registration);
            return new ResponseEntity<>(savedRegistration, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Add feedback to an event
     */
    @PostMapping("/{id}/feedback")
    public ResponseEntity<?> addFeedbackToEvent(@PathVariable("id") Long eventId,
                                                @Valid @RequestBody FeedbackDTO feedbackDTO) {
        try {
            Feedback savedFeedback = eventService.addFeedbackToEvent(eventId, feedbackDTO);
            return new ResponseEntity<>(savedFeedback, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Add material to an event
     */
    @PostMapping("/{id}/materials")
    public ResponseEntity<?> addMaterialToEvent(@PathVariable("id") Long eventId,
                                                @Valid @RequestBody Material material) {
        try {
            Material savedMaterial = eventService.addMaterialToEvent(eventId, material);
            return new ResponseEntity<>(savedMaterial, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}