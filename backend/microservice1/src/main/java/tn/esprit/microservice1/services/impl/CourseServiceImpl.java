// Source code is decompiled from a .class file using FernFlower decompiler.
package tn.esprit.microservice1.services.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.microservice1.entities.Course;
import tn.esprit.microservice1.entities.CourseProgress;
import tn.esprit.microservice1.entities.CourseStatus;
import tn.esprit.microservice1.entities.ProgressStatus;
import tn.esprit.microservice1.entities.User;
import tn.esprit.microservice1.entities.UserRole;
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

    @Transactional(
            readOnly = true
    )
    public List<Course> getAllCourses() {
        return this.courseRepository.findAll();
    }

    @Transactional(
            readOnly = true
    )
    public Course getCourseById(Long id) {
        Objects.requireNonNull(id, "Course ID cannot be null");
        return (Course)this.courseRepository.findById(id).orElseThrow(() -> {
            return new EntityNotFoundException("Course not found with id: " + id);
        });
    }

    public Course createCourse(Course course, Long instructorId) {
        Objects.requireNonNull(course, "Course cannot be null");
        Objects.requireNonNull(instructorId, "Instructor ID cannot be null");
        User instructor = (User)this.userRepository.findById(instructorId).orElseThrow(() -> {
            return new EntityNotFoundException("Instructor not found with id: " + instructorId);
        });
        if (instructor.getRole() != UserRole.INSTRUCTOR) {
            throw new IllegalStateException("User is not an instructor");
        } else {
            course.setInstructor(instructor);
            course.setStatus(CourseStatus.DRAFT);
            course.setCreatedAt(LocalDateTime.now());
            course.setUpdatedAt(LocalDateTime.now());
            course.setDifficultyScore(0.0);
            course.setEngagementScore(0.0);
            course.setCompletionRate(0.0);
            return (Course)this.courseRepository.save(course);
        }
    }

    public Course createAutomatedCourse(Course course) {
        Objects.requireNonNull(course, "Course cannot be null");
        if (!course.isAutomated()) {
            throw new IllegalArgumentException("Course must be marked as automated");
        } else if (course.getAutomatedFeatures() == null) {
            throw new IllegalArgumentException("Automated features must be specified for automated courses");
        } else {
            course.setInstructor((User)null);
            course.setStatus(CourseStatus.DRAFT);
            course.setCreatedAt(LocalDateTime.now());
            course.setUpdatedAt(LocalDateTime.now());
            course.setDifficultyScore(0.0);
            course.setEngagementScore(0.0);
            course.setCompletionRate(0.0);
            return (Course)this.courseRepository.save(course);
        }
    }

    public Course updateCourse(Long id, Course courseDetails) {
        Objects.requireNonNull(id, "Course ID cannot be null");
        Objects.requireNonNull(courseDetails, "Course details cannot be null");
        Course course = this.getCourseById(id);
        if (courseDetails.getTitle() != null) {
            course.setTitle(courseDetails.getTitle());
        }

        if (courseDetails.getDescription() != null) {
            course.setDescription(courseDetails.getDescription());
        }

        if (courseDetails.getCoverImage() != null) {
            course.setCoverImage(courseDetails.getCoverImage());
        }

        if (courseDetails.getCategory() != null) {
            course.setCategory(courseDetails.getCategory());
        }

        if (courseDetails.getPrice() != null) {
            course.setPrice(courseDetails.getPrice());
        }

        if (courseDetails.getDuration() != null) {
            course.setDuration(courseDetails.getDuration());
        }

        if (courseDetails.getLanguage() != null) {
            course.setLanguage(courseDetails.getLanguage());
        }

        if (courseDetails.getLevel() != null) {
            course.setLevel(courseDetails.getLevel());
        }

        if (courseDetails.getEstimatedCompletionTime() != null) {
            course.setEstimatedCompletionTime(courseDetails.getEstimatedCompletionTime());
        }

        if (courseDetails.getPrerequisites() != null) {
            course.setPrerequisites(courseDetails.getPrerequisites());
        }

        if (courseDetails.getLearningObjectives() != null) {
            course.setLearningObjectives(courseDetails.getLearningObjectives());
        }

        if (courseDetails.getTargetAudience() != null) {
            course.setTargetAudience(courseDetails.getTargetAudience());
        }

        if (courseDetails.getRecommendationTags() != null) {
            course.setRecommendationTags(courseDetails.getRecommendationTags());
        }

        if (courseDetails.getType() != null) {
            course.setType(courseDetails.getType());
        }

        course.setUpdatedAt(LocalDateTime.now());
        return (Course)this.courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        Objects.requireNonNull(id, "Course ID cannot be null");
        if (!this.courseRepository.existsById(id)) {
            throw new EntityNotFoundException("Course not found with id: " + id);
        } else {
            this.courseRepository.deleteById(id);
        }
    }

    public Course publishCourse(Long id) {
        Objects.requireNonNull(id, "Course ID cannot be null");
        Course course = this.getCourseById(id);
        if (course.getModules() != null && !course.getModules().isEmpty()) {
            course.setStatus(CourseStatus.PUBLISHED);
            course.setUpdatedAt(LocalDateTime.now());
            return (Course)this.courseRepository.save(course);
        } else {
            throw new IllegalStateException("Cannot publish course without modules");
        }
    }

    public Course enrollUser(Long courseId, Long userId) {
        Objects.requireNonNull(courseId, "Course ID cannot be null");
        Objects.requireNonNull(userId, "User ID cannot be null");
        Course course = this.getCourseById(courseId);
        User user = (User)this.userRepository.findById(userId).orElseThrow(() -> {
            return new EntityNotFoundException("User not found with id: " + userId);
        });
        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new IllegalStateException("Cannot enroll in unpublished course");
        } else if (course.getUserProgress().stream().anyMatch((p) -> {
            return p.getUser().getId().equals(userId);
        })) {
            throw new IllegalStateException("User is already enrolled in this course");
        } else {
            CourseProgress progress = new CourseProgress();
            progress.setUser(user);
            progress.setCourse(course);
            progress.setStatus(ProgressStatus.NOT_STARTED);
            progress.setProgressPercentage(0.0);
            progress.setEngagementScore(0.0);
            progress.setPerformanceScore(0.0);
            progress.setTimeSpentMinutes(0);
            progress.setLastAccessDate(LocalDateTime.now());
            progress = (CourseProgress)this.progressRepository.save(progress);
            course.getUserProgress().add(progress);
            user.getCourseProgresses().add(progress);
            return (Course)this.courseRepository.save(course);
        }
    }

    @Transactional(
            readOnly = true
    )
    public Set<User> getEnrolledUsers(Long courseId) {
        Objects.requireNonNull(courseId, "Course ID cannot be null");
        Course course = this.getCourseById(courseId);
        return course.getEnrolledUsers() != null ? course.getEnrolledUsers() : Collections.emptySet();
    }

    public void updateCourseMetrics(Long id) {
        Objects.requireNonNull(id, "Course ID cannot be null");
        Course course = this.getCourseById(id);
        double completionRate = this.calculateCompletionRate(course);
        double engagementScore = this.calculateEngagementScore(course);
        double difficultyScore = this.calculateDifficultyScore(course);
        course.setCompletionRate(completionRate);
        course.setEngagementScore(engagementScore);
        course.setDifficultyScore(difficultyScore);
        course.setUpdatedAt(LocalDateTime.now());
        this.courseRepository.save(course);
    }

    @Transactional(
            readOnly = true
    )
    public List<Course> getRecommendedCourses(Long userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        if (!this.userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        } else {
            List<Course> allCourses = this.courseRepository.findAll();
            return allCourses.isEmpty() ? Collections.emptyList() : (allCourses.size() > 5 ? allCourses.subList(0, 5) : allCourses);
        }
    }

    @Transactional(
            readOnly = true
    )
    public List<Course> getCoursesByInstructor(Long instructorId) {
        Objects.requireNonNull(instructorId, "Instructor ID cannot be null");
        User instructor = (User)this.userRepository.findById(instructorId).orElseThrow(() -> {
            return new EntityNotFoundException("Instructor not found with id: " + instructorId);
        });
        if (instructor.getRole() != UserRole.INSTRUCTOR) {
            throw new IllegalStateException("User is not an instructor");
        } else {
            return this.courseRepository.findByInstructor(instructor);
        }
    }

    private double calculateCompletionRate(Course course) {
        Objects.requireNonNull(course, "Course cannot be null");
        Set<CourseProgress> progresses = course.getUserProgress();
        if (progresses != null && !progresses.isEmpty()) {
            long completedCount = progresses.stream().filter((p) -> {
                return p.getStatus() == ProgressStatus.COMPLETED;
            }).count();
            return (double)completedCount / (double)progresses.size() * 100.0;
        } else {
            return 0.0;
        }
    }

    private double calculateEngagementScore(Course course) {
        Objects.requireNonNull(course, "Course cannot be null");
        Set<CourseProgress> progresses = course.getUserProgress();
        return progresses != null && !progresses.isEmpty() ? progresses.stream().mapToDouble(CourseProgress::getEngagementScore).average().orElse(0.0) : 0.0;
    }

    private double calculateDifficultyScore(Course course) {
        Objects.requireNonNull(course, "Course cannot be null");
        Set<CourseProgress> progresses = course.getUserProgress();
        return progresses != null && !progresses.isEmpty() ? progresses.stream().mapToDouble(CourseProgress::getPerformanceScore).average().orElse(0.0) : 0.0;
    }
}
