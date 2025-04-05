package tn.esprit.microservice5.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.microservice5.Model.Feedback;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDTO {
    private long id;
    private Long eventId;
    private Long userId;
    private Integer rating;
    private String comments;
    private LocalDateTime submittedAt;

    /**
     * Convert from Feedback entity to FeedbackDTO
     */
    public static FeedbackDTO fromEntity(Feedback feedback) {
        if (feedback == null) {
            return null;
        }

        return FeedbackDTO.builder()
                .id(feedback.getId())
                .eventId(feedback.getEvent() != null ? feedback.getEvent().getEventId() : null)
                .userId(feedback.getUser() != null ? feedback.getUser().getUserId() : null)
                .rating(feedback.getRating())
                .comments(feedback.getComments())
                .submittedAt(feedback.getSubmittedAt())
                .build();
    }
}