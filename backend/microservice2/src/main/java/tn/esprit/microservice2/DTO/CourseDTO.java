package tn.esprit.microservice2.DTO;

import lombok.*;
import tn.esprit.microservice2.Model.Course;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Long id;
    private String title;
    private Double price;
    private Integer durationInMonths;

    public CourseDTO(Course course) {
        if (course != null) {
            this.id = course.getId();
            this.title = course.getTitle();
            this.price = course.getPrice();
            this.durationInMonths = course.getDurationInMonths();
        }
    }

    public Long getId() {
        return id;
    }
}