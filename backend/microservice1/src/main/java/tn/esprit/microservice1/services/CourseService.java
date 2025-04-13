package tn.esprit.microservice1.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.microservice1.entities.Category;
import tn.esprit.microservice1.entities.Course;
import tn.esprit.microservice1.entities.Lesson;
import tn.esprit.microservice1.repositories.CourseRepository;
import tn.esprit.microservice1.repositories.LessonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final CategoryService categoryService;

    public CourseService(CourseRepository courseRepository, 
                         LessonRepository lessonRepository, 
                         CategoryService categoryService) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.categoryService = categoryService;
    }

    @Transactional
    public Course createCourse(Course course, Long categoryId) {
        // Validate and set free flag based on price
        if (course.getPrice() == null) {
            throw new IllegalArgumentException("Course price cannot be null");
        }
        
        // Set free flag based on price (zero price means free course)
        if (course.getPrice().compareTo(java.math.BigDecimal.ZERO) == 0) {
            course.setFree(true);
        } else {
            course.setFree(false);
        }
        
        // Set category
        Category category = categoryService.getCategoryById(categoryId);
        course.setCategory(category);
        
        // Get lessons but don't save them yet
        List<Lesson> originalLessons = null;
        if (course.getLessons() != null && !course.getLessons().isEmpty()) {
            originalLessons = new ArrayList<>(course.getLessons());
            course.setLessons(new ArrayList<>()); // Clear to avoid cascade persist errors
        }
        
        // Save course first without lessons
        Course savedCourse = courseRepository.save(course);
        
        // Now add lessons if they exist
        if (originalLessons != null && !originalLessons.isEmpty()) {
            for (Lesson lesson : originalLessons) {
                lesson.setCourse(savedCourse);
                Lesson savedLesson = lessonRepository.save(lesson);
                savedCourse.getLessons().add(savedLesson);
            }
            // Update course with the lessons
            savedCourse = courseRepository.save(savedCourse);
        }
        
        return courseRepository.findById(savedCourse.getId())
                .orElseThrow(() -> new RuntimeException("Failed to retrieve saved course"));
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
    
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Course not found with ID: " + id));
    }
    
    @Transactional
    public Course updateCourse(Long courseId, Course updatedCourse, Long categoryId) {
        // Validate that the course exists
        Course existingCourse = getCourseById(courseId);
        
        // Update fields from the request
        existingCourse.setTitle(updatedCourse.getTitle());
        existingCourse.setDescription(updatedCourse.getDescription());
        existingCourse.setPrice(updatedCourse.getPrice());
        
        // Update free flag based on price
        if (updatedCourse.getPrice().compareTo(java.math.BigDecimal.ZERO) == 0) {
            existingCourse.setFree(true);
        } else {
            existingCourse.setFree(false);
        }
        
        // Update cover image if provided
        if (updatedCourse.getCoverImage() != null) {
            existingCourse.setCoverImage(updatedCourse.getCoverImage());
        }
        
        // Update category if provided
        if (categoryId != null) {
            Category category = categoryService.getCategoryById(categoryId);
            existingCourse.setCategory(category);
        }
        
        // Handle lessons
        if (updatedCourse.getLessons() != null) {
            // Remove existing lessons that are not in the updated list
            List<Lesson> currentLessons = existingCourse.getLessons();
            List<Lesson> lessonsToRemove = new ArrayList<>();
            
            for (Lesson currentLesson : currentLessons) {
                boolean found = false;
                for (Lesson updatedLesson : updatedCourse.getLessons()) {
                    if (updatedLesson.getId() != null && updatedLesson.getId().equals(currentLesson.getId())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    lessonsToRemove.add(currentLesson);
                }
            }
            
            // Remove lessons marked for deletion
            for (Lesson lessonToRemove : lessonsToRemove) {
                existingCourse.removeLesson(lessonToRemove);
                lessonRepository.delete(lessonToRemove);
            }
            
            // Update existing lessons and add new ones
            for (Lesson updatedLesson : updatedCourse.getLessons()) {
                if (updatedLesson.getId() != null) {
                    // Existing lesson - update it
                    Optional<Lesson> existingLessonOpt = currentLessons.stream()
                            .filter(l -> l.getId().equals(updatedLesson.getId()))
                            .findFirst();
                    
                    if (existingLessonOpt.isPresent()) {
                        Lesson existingLesson = existingLessonOpt.get();
                        existingLesson.setTitle(updatedLesson.getTitle());
                        existingLesson.setContent(updatedLesson.getContent());
                        existingLesson.setOrderInCourse(updatedLesson.getOrderInCourse());
                        existingLesson.setVideoUrl(updatedLesson.getVideoUrl());
                        existingLesson.setVideoType(updatedLesson.getVideoType());
                        existingLesson.setPdfUrl(updatedLesson.getPdfUrl());
                        existingLesson.setPdfName(updatedLesson.getPdfName());
                        lessonRepository.save(existingLesson);
                    }
                } else {
                    // New lesson - add it
                    updatedLesson.setCourse(existingCourse);
                    Lesson savedLesson = lessonRepository.save(updatedLesson);
                    existingCourse.getLessons().add(savedLesson);
                }
            }
        }
        
        // Save and return the updated course
        return courseRepository.save(existingCourse);
    }
    
    @Transactional
    public void deleteCourse(Long id) {
        Course course = getCourseById(id);
        
        // Delete all lessons first to avoid orphaned records
        if (course.getLessons() != null && !course.getLessons().isEmpty()) {
            lessonRepository.deleteAll(course.getLessons());
        }
        
        // Now delete the course
        courseRepository.delete(course);
    }
    
    // Get courses by category ID
    public List<Course> getCoursesByCategory(Long categoryId) {
        // Verify that the category exists first
        categoryService.getCategoryById(categoryId);
        // Return courses with the given category
        return courseRepository.findByCategoryId(categoryId);
    }
}