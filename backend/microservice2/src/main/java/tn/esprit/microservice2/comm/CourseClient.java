package tn.esprit.microservice2.comm;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tn.esprit.microservice2.DTO.CourseDTO;

import java.util.List;

@FeignClient(name = "microservice1", fallbackFactory = CourseClientFallbackFactory.class)
public interface CourseClient {

    @GetMapping("/api/courses/{id}")
    CourseDTO getCourseById(@PathVariable("id") Long id);

    @GetMapping("/api/courses")
    List<CourseDTO> getAllCourses();
}