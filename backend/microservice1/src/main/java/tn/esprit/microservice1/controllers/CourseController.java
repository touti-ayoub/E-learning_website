// CourseController.java
package tn.esprit.microservice1.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice1.entities.Course;
import tn.esprit.microservice1.entities.Lesson;
import tn.esprit.microservice1.services.CourseService;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService courseService;
    private final Logger logger = LoggerFactory.getLogger(CourseController.class);

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * Creates a new course with optional lessons
     * 
     * @param course The course object containing details and optional lessons array
     * @param categoryId The category ID to which this course belongs
     * @return The created course with generated IDs
     */
    @PostMapping(value = "/{categoryId}", consumes = "application/json")
    public ResponseEntity<?> createCourse(
            @RequestBody Course course,
            @PathVariable Long categoryId) {
        try {
            logger.info("Creating course with title: {} in category: {}", course.getTitle(), categoryId);
            logger.debug("Course data: {}", course);
            if (course.getLessons() != null) {
                logger.info("Course has {} lessons", course.getLessons().size());
            }
            
            Course createdCourse = courseService.createCourse(course, categoryId);
            return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Bad request error creating course: {}", e.getMessage());
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error creating course", e);
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all courses
     * 
     * @return List of all courses
     */
    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }
    
    /**
     * Get a course by its ID
     * 
     * @param id The course ID
     * @return The course if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        try {
            Course course = courseService.getCourseById(id);
            return ResponseEntity.ok(course);
        } catch (NoSuchElementException e) {
            logger.error("Course not found: {}", e.getMessage());
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error retrieving course", e);
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Update an existing course
     * 
     * @param id The course ID to update
     * @param course The updated course data
     * @param categoryId The category ID (optional)
     * @return The updated course
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(
            @PathVariable Long id,
            @RequestBody Course course,
            @RequestParam(required = false) Long categoryId) {
        try {
            logger.info("Updating course with ID: {}", id);
            Course updatedCourse = courseService.updateCourse(id, course, categoryId);
            return ResponseEntity.ok(updatedCourse);
        } catch (NoSuchElementException e) {
            logger.error("Course not found: {}", e.getMessage());
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            logger.error("Bad request error updating course: {}", e.getMessage());
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error updating course", e);
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Delete a course
     * 
     * @param id The course ID to delete
     * @return Success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        try {
            logger.info("Deleting course with ID: {}", id);
            courseService.deleteCourse(id);
            return ResponseEntity.ok(Map.of("message", "Course deleted successfully"));
        } catch (NoSuchElementException e) {
            logger.error("Course not found: {}", e.getMessage());
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error deleting course", e);
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}