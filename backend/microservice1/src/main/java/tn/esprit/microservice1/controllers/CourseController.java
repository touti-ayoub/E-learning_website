package tn.esprit.microservice1.controllers;

import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.microservice1.entities.Course;
import tn.esprit.microservice1.entities.User;
import tn.esprit.microservice1.services.CourseService;

@RestController
@RequestMapping({"/courses"})
@CrossOrigin({"*"})
public class CourseController {
    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping({"/type/automated"})
    public ResponseEntity<Course> createAutomatedCourse(@RequestBody Course course) {
        course.setAutomated(true);
        return new ResponseEntity(this.courseService.createAutomatedCourse(course), HttpStatus.CREATED);
    }

    @GetMapping({"/instructor/{instructorId}"})
    public ResponseEntity<List<Course>> getCoursesByInstructor(@PathVariable Long instructorId) {
        return ResponseEntity.ok(this.courseService.getCoursesByInstructor(instructorId));
    }

    @PostMapping({"/instructor/{instructorId}"})
    public ResponseEntity<Course> createInstructorCourse(@RequestBody Course course, @PathVariable Long instructorId) {
        course.setAutomated(false);
        return new ResponseEntity(this.courseService.createCourse(course, instructorId), HttpStatus.CREATED);
    }

    @GetMapping({"/recommended/{userId}"})
    public ResponseEntity<List<Course>> getRecommendedCourses(@PathVariable Long userId) {
        return ResponseEntity.ok(this.courseService.getRecommendedCourses(userId));
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(this.courseService.getAllCourses());
    }

    @GetMapping({"/{id}"})
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(this.courseService.getCourseById(id));
    }

    @PutMapping({"/{id}"})
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        return ResponseEntity.ok(this.courseService.updateCourse(id, course));
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        this.courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping({"/{id}/publish"})
    public ResponseEntity<Course> publishCourse(@PathVariable Long id) {
        return ResponseEntity.ok(this.courseService.publishCourse(id));
    }

    @PostMapping({"/{courseId}/enroll/{userId}"})
    public ResponseEntity<Course> enrollUser(@PathVariable Long courseId, @PathVariable Long userId) {
        return ResponseEntity.ok(this.courseService.enrollUser(courseId, userId));
    }

    @GetMapping({"/{courseId}/enrolled-users"})
    public ResponseEntity<Set<User>> getEnrolledUsers(@PathVariable Long courseId) {
        return ResponseEntity.ok(this.courseService.getEnrolledUsers(courseId));
    }

    @PostMapping({"/{id}/update-metrics"})
    public ResponseEntity<Void> updateCourseMetrics(@PathVariable Long id) {
        this.courseService.updateCourseMetrics(id);
        return ResponseEntity.ok().build();
    }
}
