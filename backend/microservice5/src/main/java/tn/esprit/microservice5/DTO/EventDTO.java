package tn.esprit.microservice5.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.microservice5.Model.Event;
import tn.esprit.microservice5.Model.EventType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private long eventId;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxCapacity;
    private String place;
    private EventType eventType;
    private String googleCalendarEventId;
    private int registrationsCount;
    private int feedbacksCount;
    private int materialsCount;
    private double averageRating;

    /**
     * Convert from Event entity to EventDTO
     */
    public static EventDTO fromEntity(Event event) {
        if (event == null) {
            return null;
        }

        // Calculate average rating if there are feedbacks
        double avgRating = 0.0;
        if (event.getFeedbacks() != null && !event.getFeedbacks().isEmpty()) {
            avgRating = event.getFeedbacks().stream()
                    .mapToInt(feedback -> feedback.getRating())
                    .average()
                    .orElse(0.0);
        }

        return EventDTO.builder()
                .eventId(event.getEventId())
                .title(event.getTitle())
                .description(event.getDescription())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .maxCapacity(event.getMaxCapacity())
                .place(event.getPlace())
                .eventType(event.getEventType())
                .googleCalendarEventId(event.getGoogleCalendarEventId())
                .registrationsCount(event.getRegistrations() != null ? event.getRegistrations().size() : 0)
                .feedbacksCount(event.getFeedbacks() != null ? event.getFeedbacks().size() : 0)
                .materialsCount(event.getMaterials() != null ? event.getMaterials().size() : 0)
                .averageRating(avgRating)
                .build();
    }
}