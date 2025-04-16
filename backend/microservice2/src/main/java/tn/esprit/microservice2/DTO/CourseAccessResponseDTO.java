package tn.esprit.microservice2.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseAccessResponseDTO {
    private boolean hasAccess;
    private String message = "";
    private Long userId;
    private Long courseId;
} 