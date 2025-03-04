package tn.esprit.microservice1.services;

import tn.esprit.microservice1.entities.Course;
import tn.esprit.microservice1.entities.User;
import java.util.*;

public interface CourseService {
    List<Course> findAll();
    
    Course findById(Long id);
    
    Course save(Course course);
    
    Course update(Course course);
    
    void deleteById(Long id);
    
    Course createAutomatedCourse(Course course);
    
    List<Course> getCoursesByInstructor(Long instructorId);
    
    Course createCourse(Course course, Long instructorId);
    
    List<Course> getRecommendedCourses(Long userId);
    
    Course publishCourse(Long id);
    
    Course enrollUser(Long courseId, Long userId);
    
    Set<User> getEnrolledUsers(Long courseId);
    
    void updateCourseMetrics(Long id);
    
    Map<String, Object> getOverviewStats();
    
    List<Map<String, Object>> getCoursesByCategory();
    
    List<Course> getRecentCourses(int limit);
    
    List<Course> getTopPerformingCourses(int limit);
} 