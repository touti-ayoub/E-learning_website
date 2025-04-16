package tn.esprit.microservice5.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.microservice5.Model.Event;
import tn.esprit.microservice5.Repo.IEventRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private IEventRepo eventRepository;

    // Create
    public Event createEvent(Event event) {
        if (event.getEndTime().isBefore(event.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        return eventRepository.save(event);
    }

    // Read
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
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
}