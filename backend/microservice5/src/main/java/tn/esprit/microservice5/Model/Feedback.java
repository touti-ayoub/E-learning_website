package tn.esprit.microservice5.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    //@NotNull(message = "Event is required")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "User is required")
    private User user;

    @NotNull(message = "Rating is required")
    private Integer rating;

    private String comments;

    @NotNull(message = "Submission date is required")
    private LocalDateTime submittedAt;

}