package tn.esprit.microservice5.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.microservice5.Model.Registration;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO {
    private long id;
    private Long eventId;
    private Long userId;
    /**
     * Convert from Registration entity to RegistrationDTO
     */
    public static RegistrationDTO fromEntity(Registration registration) {
        if (registration == null) {
            return null;
        }

        return RegistrationDTO.builder()
                .id(registration.getId())
                .eventId(registration.getEvent() != null ? registration.getEvent().getEventId() : null)
                .userId(registration.getUser() != null ? registration.getUser().getUserId() : null)
                .build();
    }
}