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
    public EventDTO createEvent(@RequestBody EventDTO dto) {
        Event createdEvent = eventService.createEvent(dto);
        return EventDTO.fromEntity(createdEvent);
    }

    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<EventDTO> eventDTOs = eventService.getAllEvents();
        return new ResponseEntity<>(eventDTOs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable("id") Long eventId) {
        EventDTO eventDTO = eventService.getEventById(eventId);
        if (eventDTO != null) {
            return new ResponseEntity<>(eventDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public EventDTO updateEvent(@PathVariable("id") Long eventId,
                                @RequestBody EventDTO dto) {
        Event updatedEvent = eventService.updateEvent(eventId, dto);
        return EventDTO.fromEntity(updatedEvent);
    }

    @DeleteMapping("/{id}")
    public String deleteEvent(@PathVariable("id") Long eventId) {
        eventService.deleteEvent(eventId);
        return "Event with ID " + eventId + " was deleted.";
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