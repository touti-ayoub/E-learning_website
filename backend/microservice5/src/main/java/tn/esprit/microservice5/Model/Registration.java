package tn.esprit.microservice5.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @NotNull(message = "Event is required")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "User is required")
    private User user;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Registration status is required")
    private RegistrationStatus status;

    @NotNull(message = "Registration date is required")
    private LocalDateTime registrationDate;

    @NotNull(message = "Payment status is required")
    private boolean paymentStatus;

}