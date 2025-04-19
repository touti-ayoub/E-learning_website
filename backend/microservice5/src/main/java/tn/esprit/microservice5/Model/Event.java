package tn.esprit.microservice5.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long eventId;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    @Column(nullable = false)
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(length = 1000)
    private String description;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    @Column(nullable = false)
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    @Column(nullable = false)
    private LocalDateTime endTime;

    @Min(value = 1, message = "Maximum capacity must be at least 1")
    @Max(value = 10000, message = "Maximum capacity cannot exceed 10000")
    private Integer maxCapacity;

    @NotBlank(message = "Place is required")
    @Size(min = 2, max = 200, message = "Place must be between 2 and 200 characters")
    private String place;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Event type is required")
    private EventType eventType;

    private String meetingLink;


    // -------------- New field to store the Google Calendar event ID --------------
    private String googleCalendarEventId;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Registration> registrations = new ArrayList<>();




}