package tn.esprit.microservice2.comm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import tn.esprit.microservice2.DTO.CourseDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class CourseClientFallbackFactory implements FallbackFactory<CourseClient> {

    private static final Logger logger = LoggerFactory.getLogger(CourseClientFallbackFactory.class);

    @Override
    public CourseClient create(Throwable cause) {
        return new CourseClient() {
            @Override
            public CourseDTO getCourseById(Long id) {
                logger.error("Error fetching course by ID: {}, cause: {}", id, cause.getMessage());
                // Return a fallback course with minimal data
                CourseDTO fallback = new CourseDTO();
                fallback.setId(id);
                fallback.setTitle("Unavailable Course");
                fallback.setPrice(BigDecimal.ZERO);
                return fallback;
            }

            @Override
            public List<CourseDTO> getAllCourses() {
                logger.error("Error fetching all courses, cause: {}", cause.getMessage());
                // Return empty list as fallback
                return new ArrayList<>();
            }
        };
    }
}