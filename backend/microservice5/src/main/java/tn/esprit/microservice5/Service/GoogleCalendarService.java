package tn.esprit.microservice5.Service;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.client.util.DateTime;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "CalendarProject";
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private Calendar getCalendarService() throws GeneralSecurityException, IOException {
        ClassPathResource resource = new ClassPathResource("service-account.json");
        try (InputStream inputStream = resource.getInputStream()) {
            ServiceAccountCredentials serviceAccountCredentials =
                    (ServiceAccountCredentials) ServiceAccountCredentials.fromStream(inputStream)
                            .createScoped(Collections.singleton(CalendarScopes.CALENDAR));

            return new Calendar.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    new HttpCredentialsAdapter(serviceAccountCredentials)
            )
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        }
    }

    /**
     * Inserts a new event into Google Calendar and returns the event ID.
     *
     * @param calendarId        e.g. "primary" or a custom calendar's ID
     * @param summary           The event summary/title
     * @param description       Event description
     * @param startDateTimeStr  ISO 8601 start date/time (e.g. 2025-04-10T10:00:00-07:00)
     * @param endDateTimeStr    ISO 8601 end date/time
     * @return The Google Calendar event ID of the created event
     */
    public String createEvent(String calendarId, String summary, String description,
                              String startDateTimeStr, String endDateTimeStr)
            throws GeneralSecurityException, IOException {

        Calendar service = getCalendarService();

        // Prepare the event
        Event event = new Event()
                .setSummary(summary)
                .setDescription(description);

        // Start & end times
        DateTime startDateTime = new DateTime(startDateTimeStr);
        EventDateTime start = new EventDateTime().setDateTime(startDateTime);
        event.setStart(start);

        DateTime endDateTime = new DateTime(endDateTimeStr);
        EventDateTime end = new EventDateTime().setDateTime(endDateTime);
        event.setEnd(end);

        // Insert the event
        Event createdEvent = service.events().insert(calendarId, event).execute();

        // Return the ID of the created event (for storing in the DB)
        System.out.println("Created event in Calendar ID: " + calendarId +
                " with GoogleEventID = " + createdEvent.getId());

        return createdEvent.getId();
    }

    public Event updateEvent(String calendarId,
                             String eventId,       // The googleCalendarEventId
                             String newSummary,
                             String newDescription,
                             String newStartDateTimeStr,
                             String newEndDateTimeStr)
            throws GeneralSecurityException, IOException {

        Calendar service = getCalendarService();

        // First, retrieve the existing event from Google Calendar
        Event event = service.events().get(calendarId, eventId).execute();
        if (event == null) {
            throw new IllegalArgumentException("No event found in calendar with ID: " + eventId);
        }

        // Update the fields you want to change
        event.setSummary(newSummary);
        event.setDescription(newDescription);

        // Update start/end times
        if (newStartDateTimeStr != null) {
            event.setStart(new EventDateTime().setDateTime(new DateTime(newStartDateTimeStr)));
        }
        if (newEndDateTimeStr != null) {
            event.setEnd(new EventDateTime().setDateTime(new DateTime(newEndDateTimeStr)));
        }

        // Send the update request
        Event updatedEvent = service.events().update(calendarId, eventId, event).execute();
        return updatedEvent;
    }

    public void deleteEvent(String calendarId, String eventId)
            throws GeneralSecurityException, IOException {

        Calendar service = getCalendarService();
        service.events().delete(calendarId, eventId).execute();
    }

}
