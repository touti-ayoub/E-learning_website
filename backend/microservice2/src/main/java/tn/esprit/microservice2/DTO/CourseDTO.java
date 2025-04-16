package tn.esprit.microservice2.DTO;

import lombok.*;
import tn.esprit.microservice2.Model.Course;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Long id;
    private String title;
    private BigDecimal price;

    public CourseDTO(Course course) {
        if (course != null) {
            this.id = course.getId();
            this.title = course.getTitle();
            this.price = course.getPrice();
        }
    }

    public Long getId() {
        return id;
    }
}