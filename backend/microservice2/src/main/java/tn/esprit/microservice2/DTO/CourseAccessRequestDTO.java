package tn.esprit.microservice2.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseAccessRequestDTO {
    private Long userId;
    private Long courseId;
} 