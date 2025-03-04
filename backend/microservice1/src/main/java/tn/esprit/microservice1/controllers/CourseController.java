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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.microservice1.entities.Course;
import tn.esprit.microservice1.entities.User;
import tn.esprit.microservice1.services.CourseService;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
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
        return ResponseEntity.ok(this.courseService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(this.courseService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(
        @RequestParam("file") MultipartFile file,
        @RequestParam("course") Course course) {
        // Logic to save the file
        String filePath = saveFile(file); // Implement this method to save the file and return the path
        course.setCoverImage(filePath); // Assuming there's a field for the image path in the Course entity

        return ResponseEntity.ok(this.courseService.save(course));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        course.setId(id);
        return ResponseEntity.ok(this.courseService.update(course));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        this.courseService.deleteById(id);
        return ResponseEntity.ok().build();
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

    @GetMapping("/stats/overview")
    public ResponseEntity<Map<String, Object>> getOverviewStats() {
        return ResponseEntity.ok(this.courseService.getOverviewStats());
    }

    @GetMapping("/stats/by-category")
    public ResponseEntity<List<Map<String, Object>>> getCoursesByCategory() {
        return ResponseEntity.ok(this.courseService.getCoursesByCategory());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Course>> getRecentCourses(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(this.courseService.getRecentCourses(limit));
    }

    @GetMapping("/top-performing")
    public ResponseEntity<List<Course>> getTopPerformingCourses(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(this.courseService.getTopPerformingCourses(limit));
    }

    private String saveFile(MultipartFile file) {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String uploadDir = "/actual/path/to/upload/directory"; // Update this path to your desired upload directory

        try {
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs(); // Create the directory if it doesn't exist
            }
            File destinationFile = new File(uploadDir, fileName);
            file.transferTo(destinationFile); // Save the file
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }

        return fileName; // Return the file name or path as needed
    }
}
