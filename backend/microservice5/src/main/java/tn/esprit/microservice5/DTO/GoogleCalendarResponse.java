package tn.esprit.microservice5.DTO;

public class GoogleCalendarResponse {
    private String eventId;
    private String meetingLink;

    public GoogleCalendarResponse(String eventId, String meetingLink) {
        this.eventId = eventId;
        this.meetingLink = meetingLink;
    }

    // Getters
    public String getEventId() {
        return eventId;
    }

    public String getMeetingLink() {
        return meetingLink;
    }
}