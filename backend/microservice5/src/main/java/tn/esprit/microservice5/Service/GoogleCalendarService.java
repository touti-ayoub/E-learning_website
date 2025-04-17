package tn.esprit.microservice5.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;
import org.springframework.stereotype.Service;
import tn.esprit.microservice5.DTO.GoogleCalendarResponse;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;



@Service
public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "CalendarProject";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();    private static final List<String> SCOPES = Arrays.asList(
            CalendarScopes.CALENDAR,
            CalendarScopes.CALENDAR_EVENTS
    );
    private static final String CREDENTIALS_FILE_PATH = "/service-account.json";
    private static final java.io.File TOKENS_DIRECTORY = new java.io.File("tokens");

    /**
     * Performs OAuth2 authorization (opens a browser once, then caches tokens).
     */
    private Credential authorize() throws IOException, GeneralSecurityException {
        // Load client secrets.
        InputStream in = GoogleCalendarService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY, new InputStreamReader(in)
        );

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                clientSecrets,
                SCOPES
        )
                .setDataStoreFactory(new FileDataStoreFactory(TOKENS_DIRECTORY))
                .setAccessType("offline")
                .build();

        // Start local server to receive OAuth callback.
        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(8888)
                .build();

        // Authorize and return the Credential.
        return new AuthorizationCodeInstalledApp(flow, receiver)
                .authorize("user");
    }

    /**
     * Builds and returns a Calendar service object authorized with OAuth2 credentials.
     */
    private Calendar getCalendarService() throws IOException, GeneralSecurityException {
        Credential credential = authorize();
        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential
        )
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Creates a new event in the given calendar, with a Google Meet link.
     *
     * @return the ID of the created Google Calendar event
     */
    public GoogleCalendarResponse createEvent(
            String calendarId,
            String summary,
            String description,
            String startDateTimeIso,
            String endDateTimeIso,
            String eventType
    ) throws IOException, GeneralSecurityException {
        Calendar service = getCalendarService();

        // Build the event
        Event event = new Event()
                .setSummary(summary)
                .setDescription(description);

        // Set start & end times
        event.setStart(new EventDateTime().setDateTime(new DateTime(startDateTimeIso)));
        event.setEnd(new EventDateTime().setDateTime(new DateTime(endDateTimeIso)));

        // Only add Google Meet conference if event type is WEBINAR
        boolean isWebinar = "WEBINAR".equalsIgnoreCase(eventType);
        if (isWebinar) {
            ConferenceSolutionKey solutionKey = new ConferenceSolutionKey().setType("hangoutsMeet");
            CreateConferenceRequest createRequest = new CreateConferenceRequest()
                    .setRequestId(UUID.randomUUID().toString())
                    .setConferenceSolutionKey(solutionKey);
            ConferenceData conferenceData = new ConferenceData().setCreateRequest(createRequest);
            event.setConferenceData(conferenceData);
        }

        // Insert with conferenceDataVersion=1 only when it's a webinar
        Event created = service.events()
                .insert(calendarId, event)
                .setConferenceDataVersion(isWebinar ? 1 : 0)
                .execute();

        // Extract meeting link if it's a webinar
        String meetingLink = null;
        if (isWebinar && created.getConferenceData() != null &&
                created.getConferenceData().getEntryPoints() != null &&
                !created.getConferenceData().getEntryPoints().isEmpty()) {
            meetingLink = created.getConferenceData().getEntryPoints().stream()
                    .filter(ep -> "video".equals(ep.getEntryPointType()))
                    .map(EntryPoint::getUri)
                    .findFirst()
                    .orElse(null);
        }

        return new GoogleCalendarResponse(created.getId(), meetingLink);
    }

    /**
     * Updates an existing event. Also supports updating its conferenceData.
     */
    public Event updateEvent(
            String calendarId,
            String eventId,
            String newSummary,
            String newDescription,
            String newStartIso,
            String newEndIso
    ) throws IOException, GeneralSecurityException {
        Calendar service = getCalendarService();

        // Fetch the existing event
        Event event = service.events().get(calendarId, eventId).execute();
        if (event == null) {
            throw new IllegalArgumentException("No event found with ID: " + eventId);
        }

        // Update fields
        event.setSummary(newSummary);
        event.setDescription(newDescription);
        if (newStartIso != null) {
            event.setStart(new EventDateTime().setDateTime(new DateTime(newStartIso)));
        }
        if (newEndIso != null) {
            event.setEnd(new EventDateTime().setDateTime(new DateTime(newEndIso)));
        }

        // Push update (include conferenceDataVersion if you want to modify conferencing)
        return service.events()
                .update(calendarId, eventId, event)
                .setConferenceDataVersion(1)
                .execute();
    }

    /**
     * Deletes an event from the calendar.
     */
    public void deleteEvent(String calendarId, String eventId)
            throws IOException, GeneralSecurityException {
        getCalendarService()
                .events()
                .delete(calendarId, eventId)
                .execute();
    }

}
