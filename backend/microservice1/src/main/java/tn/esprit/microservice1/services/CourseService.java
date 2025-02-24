package tn.esprit.microservice1.services;

import java.util.List;
import java.util.Set;
import tn.esprit.microservice1.entities.Course;
import tn.esprit.microservice1.entities.User;

public interface CourseService {
    List<Course> getAllCourses();

    Course getCourseById(Long id);

    Course createCourse(Course course, Long instructorId);

    Course createAutomatedCourse(Course course);

    Course updateCourse(Long id, Course course);

    void deleteCourse(Long id);

    Course publishCourse(Long id);

    Course enrollUser(Long courseId, Long userId);

    Set<User> getEnrolledUsers(Long courseId);

    void updateCourseMetrics(Long id);

    List<Course> getRecommendedCourses(Long userId);

    List<Course> getCoursesByInstructor(Long instructorId);
}
