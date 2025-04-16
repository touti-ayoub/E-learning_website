package tn.esprit.microservice5.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.microservice5.DTO.EventDTO;
import tn.esprit.microservice5.DTO.FeedbackDTO;
import tn.esprit.microservice5.Model.*;
import tn.esprit.microservice5.Repo.IEventRepo;
import tn.esprit.microservice5.Repo.IFeedbackRepository;
import tn.esprit.microservice5.Repo.IMaterialRepo;
import tn.esprit.microservice5.Repo.IRegistrationRepo;
import tn.esprit.microservice5.Repo.IUserRepo;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final GoogleCalendarService googleCalendarService;

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

    public EventService(GoogleCalendarService googleCalendarService) {
        this.googleCalendarService = googleCalendarService;
    }

    /**
     * Creates a new event, saves it in the DB,
     * and also inserts it into Google Calendar.
     *
     * @param eventDTO Data for the new event
     * @return The saved Event entity (or you could return an EventDTO)
     */
    // Create
    @Transactional
    public Event createEvent(EventDTO eventDTO) {
        // 1) Build the Event entity (as you do now)
        Event event = new Event();
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setStartTime(eventDTO.getStartTime());
        event.setEndTime(eventDTO.getEndTime());
        event.setMaxCapacity(eventDTO.getMaxCapacity());
        event.setPlace(eventDTO.getPlace());
        event.setEventType(eventDTO.getEventType());

        // Save to DB
        eventRepository.save(event);

        try {
            // 2) Convert LocalDateTime to ZonedDateTime
            ZonedDateTime startZdt = eventDTO.getStartTime()
                    .atZone(ZoneId.systemDefault());
            ZonedDateTime endZdt = eventDTO.getEndTime()
                    .atZone(ZoneId.systemDefault());

            // 3) Format as ISO_OFFSET_DATE_TIME
            String startIso = startZdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            String endIso = endZdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            // Now pass fully qualified date strings (e.g. "2025-05-01T10:00:00-04:00")
            String calendarId = "db5783463b512af762897b63c4d31b3ddbc6a07a52169598009f21ec6f5543e2@group.calendar.google.com";

            String createdEventId = googleCalendarService.createEvent(
                    calendarId,
                    eventDTO.getTitle(),
                    eventDTO.getDescription(),
                    startIso,
                    endIso
            );

            event.setGoogleCalendarEventId(createdEventId);
            eventRepository.save(event);

        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        return event;
    }

    // Read - Updated to return DTOs instead of entities
    public List<EventDTO> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return events.stream()
                .map(EventDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Get event by ID as DTO
    public EventDTO getEventById(Long id) {
        return eventRepository.findById(id)
                .map(EventDTO::fromEntity)
                .orElse(null);
    }

    // Update
    @Transactional
    public Event updateEvent(Long eventId, EventDTO newEventData) {
        // 1) Retrieve the existing event
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));

        // 2) Update fields
        existingEvent.setTitle(newEventData.getTitle());
        existingEvent.setDescription(newEventData.getDescription());
        existingEvent.setStartTime(newEventData.getStartTime());
        existingEvent.setEndTime(newEventData.getEndTime());
        existingEvent.setMaxCapacity(newEventData.getMaxCapacity());
        existingEvent.setPlace(newEventData.getPlace());
        existingEvent.setEventType(newEventData.getEventType());

        // 3) Save to DB
        eventRepository.save(existingEvent);

        // 4) If there's a googleCalendarEventId, update the Calendar entry
        if (existingEvent.getGoogleCalendarEventId() != null) {
            try {
                // Step A: Convert to ZonedDateTime
                ZonedDateTime startZdt = newEventData.getStartTime().atZone(ZoneId.systemDefault());
                ZonedDateTime endZdt = newEventData.getEndTime().atZone(ZoneId.systemDefault());

                // Step B: Format as ISO 8601 with offset
                String startIso = startZdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                String endIso = endZdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

                // Step C: Update the event in Google Calendar using the same calendar ID
                // that the event was originally created in.
                String calendarId = "db5783463b512af762897b63c4d31b3ddbc6a07a52169598009f21ec6f5543e2@group.calendar.google.com"; // or custom ID

                googleCalendarService.updateEvent(
                        calendarId,
                        existingEvent.getGoogleCalendarEventId(),
                        existingEvent.getTitle(),
                        existingEvent.getDescription(),
                        startIso,
                        endIso
                );

            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
                // Optionally handle failures (retry, log, etc.)
            }
        }

        // Return updated entity
        return existingEvent;
    }

    // Delete
    @Transactional
    public void deleteEvent(Long eventId) {
        // 1) Find the event
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));

        // 2) Delete from Google Calendar if possible
        if (existingEvent.getGoogleCalendarEventId() != null) {
            try {
                String calendarId = "db5783463b512af762897b63c4d31b3ddbc6a07a52169598009f21ec6f5543e2@group.calendar.google.com";
                googleCalendarService.deleteEvent(calendarId, existingEvent.getGoogleCalendarEventId());
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
                // Decide if you want to abort the DB delete if Calendar delete fails
            }
        }

        // 3) Delete from the DB
        eventRepository.delete(existingEvent);
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