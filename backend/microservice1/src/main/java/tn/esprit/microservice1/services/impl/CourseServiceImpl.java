package tn.esprit.microservice1.services.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.microservice1.entities.*;
import tn.esprit.microservice1.repositories.CourseProgressRepository;
import tn.esprit.microservice1.repositories.CourseRepository;
import tn.esprit.microservice1.repositories.UserRepository;
import tn.esprit.microservice1.services.CourseService;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseProgressRepository progressRepository;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository, UserRepository userRepository, CourseProgressRepository progressRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.progressRepository = progressRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Course findById(Long id) {
        Objects.requireNonNull(id, "Course ID cannot be null");
        return courseRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + id));
    }

    @Override
    public Course save(Course course) {
        Objects.requireNonNull(course, "Course cannot be null");
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }

    @Override
    public Course update(Course course) {
        Objects.requireNonNull(course, "Course cannot be null");
        Objects.requireNonNull(course.getId(), "Course ID cannot be null");
        
        if (!courseRepository.existsById(course.getId())) {
            throw new EntityNotFoundException("Course not found with id: " + course.getId());
        }
        Course existingCourse = findById(course.getId());
        course.setCreatedAt(existingCourse.getCreatedAt());
        course.setUpdatedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }

    @Override
    public void deleteById(Long id) {
        Objects.requireNonNull(id, "Course ID cannot be null");
        if (!courseRepository.existsById(id)) {
            throw new EntityNotFoundException("Course not found with id: " + id);
        }
        Course course = findById(id);
        courseRepository.delete(course);
    }

    @Override
    public Course createAutomatedCourse(Course course) {
        Objects.requireNonNull(course, "Course cannot be null");
        course.setAutomated(true);
        course.setStatus(CourseStatus.DRAFT);
        
        // Initialize automated features
        AutomatedFeatures features = new AutomatedFeatures();
        features.setHasAutoGrading(true);
        features.setHasAdaptiveLearning(true);
        features.setHasPeerReview(false);
        course.setAutomatedFeatures(features);
        System.out.println("Course details before saving: " +
        "Title: " + course.getTitle() +
        ", Description: " + course.getDescription() +
        ", Rating: " + course.getRating() + // Add this line
        ", Duration: " + course.getDuration() +
        ", ..."); // Include other relevant fields
        return save(course);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> getCoursesByInstructor(Long instructorId) {
        Objects.requireNonNull(instructorId, "Instructor ID cannot be null");
        User instructor = userRepository.findById(instructorId)
            .orElseThrow(() -> new EntityNotFoundException("Instructor not found with id: " + instructorId));
        if (instructor.getRole() != UserRole.INSTRUCTOR) {
            throw new IllegalStateException("User is not an instructor");
        }
        return courseRepository.findByInstructor(instructor);
    }

    @Override
    public Course createCourse(Course course, Long instructorId) {
        Objects.requireNonNull(course, "Course cannot be null");

        if (course.isAutomated()) {
            // Skip instructor validation if automated
            course.setInstructor(null); // or handle as needed
        } else {
            Objects.requireNonNull(instructorId, "Instructor ID cannot be null");
            User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new EntityNotFoundException("Instructor not found with id: " + instructorId));
            course.setInstructor(instructor);
        }

        course.setStatus(CourseStatus.DRAFT);
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> getRecommendedCourses(Long userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
        // Get user's enrolled courses
        Set<Course> enrolledCourses = user.getEnrolledCourses();
        
        // Get user's completed courses categories
        Set<String> userCategories = enrolledCourses.stream()
                .map(Course::getCategory)
                .collect(Collectors.toSet());
        
        // Find similar courses in the same categories
        return courseRepository.findAll().stream()
                .filter(course -> !enrolledCourses.contains(course))
                .filter(course -> userCategories.contains(course.getCategory()))
                .sorted(Comparator.comparing(Course::getEngagementScore).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    @Override
    public Course publishCourse(Long id) {
        Objects.requireNonNull(id, "Course ID cannot be null");
        Course course = findById(id);
        
        if (course.getModules() == null || course.getModules().isEmpty()) {
            throw new IllegalStateException("Cannot publish course without modules");
        }
        
        course.setStatus(CourseStatus.PUBLISHED);
        course.setUpdatedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }

    @Override
    public Course enrollUser(Long courseId, Long userId) {
        Objects.requireNonNull(courseId, "Course ID cannot be null");
        Objects.requireNonNull(userId, "User ID cannot be null");
        
        Course course = findById(courseId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new IllegalStateException("Cannot enroll in unpublished course");
        }
        
        if (course.getUserProgress().stream().anyMatch(p -> p.getUser().getId().equals(userId))) {
            throw new IllegalStateException("User is already enrolled in this course");
        }
        
        CourseProgress progress = new CourseProgress();
        progress.setUser(user);
        progress.setCourse(course);
        progress.setStartedAt(LocalDateTime.now());
        progress.setProgressPercentage(0.0);
        progress.setStatus(ProgressStatus.IN_PROGRESS);
        
        progressRepository.save(progress);
        
        course.getEnrolledUsers().add(user);
        return courseRepository.save(course);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<User> getEnrolledUsers(Long courseId) {
        Objects.requireNonNull(courseId, "Course ID cannot be null");
        Course course = findById(courseId);
        return course.getEnrolledUsers();
    }

    @Override
    public void updateCourseMetrics(Long id) {
        Objects.requireNonNull(id, "Course ID cannot be null");
        Course course = findById(id);
        
        double completionRate = calculateCompletionRate(course);
        double engagementScore = calculateEngagementScore(course);
        double difficultyScore = calculateDifficultyScore(course);
        
        course.setCompletionRate(completionRate);
        course.setEngagementScore(engagementScore);
        course.setDifficultyScore(difficultyScore);
        course.setUpdatedAt(LocalDateTime.now());
        
        courseRepository.save(course);
    }

    @Override
    public Map<String, Object> getOverviewStats() {
        List<Course> allCourses = findAll();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCourses", allCourses.size());
        stats.put("totalStudents", allCourses.stream()
                .mapToInt(c -> c.getEnrolledUsers().size())
                .sum());
        stats.put("averageCompletionRate", allCourses.stream()
                .mapToDouble(Course::getCompletionRate)
                .average()
                .orElse(0.0));
        stats.put("averageEngagementScore", allCourses.stream()
                .mapToDouble(Course::getEngagementScore)
                .average()
                .orElse(0.0));
        
        return stats;
    }

    @Override
    public List<Map<String, Object>> getCoursesByCategory() {
        Map<String, Long> categoryCounts = findAll().stream()
                .collect(Collectors.groupingBy(
                        Course::getCategory,
                        Collectors.counting()
                ));
        
        return categoryCounts.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> categoryStats = new HashMap<>();
                    categoryStats.put("category", entry.getKey());
                    categoryStats.put("count", entry.getValue());
                    return categoryStats;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Course> getRecentCourses(int limit) {
        return courseRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<Course> getTopPerformingCourses(int limit) {
        return findAll().stream()
                .sorted(Comparator.comparing(Course::getEngagementScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    private double calculateCompletionRate(Course course) {
        Objects.requireNonNull(course, "Course cannot be null");
        Set<CourseProgress> progresses = course.getUserProgress();
        if (progresses != null && !progresses.isEmpty()) {
            long completedCount = progresses.stream()
                .filter(p -> p.getStatus() == ProgressStatus.COMPLETED)
                .count();
            return (double) completedCount / progresses.size() * 100.0;
        }
        return 0.0;
    }

    private double calculateEngagementScore(Course course) {
        Objects.requireNonNull(course, "Course cannot be null");
        Set<CourseProgress> progresses = course.getUserProgress();
        if (progresses != null && !progresses.isEmpty()) {
            return progresses.stream()
                .mapToDouble(CourseProgress::getEngagementScore)
                .average()
                .orElse(0.0);
        }
        return 0.0;
    }

    private double calculateDifficultyScore(Course course) {
        Objects.requireNonNull(course, "Course cannot be null");
        Set<CourseProgress> progresses = course.getUserProgress();
        if (progresses != null && !progresses.isEmpty()) {
            return progresses.stream()
                .mapToDouble(CourseProgress::getPerformanceScore)
                .average()
                .orElse(0.0);
        }
        return 0.0;
    }
}
