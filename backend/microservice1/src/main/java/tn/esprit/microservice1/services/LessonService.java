package tn.esprit.microservice1.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.microservice1.entities.Course;
import tn.esprit.microservice1.entities.Lesson;
import tn.esprit.microservice1.repositories.LessonRepository;

import java.io.IOException;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class LessonService {
    
    private final LessonRepository lessonRepository;
    private final CourseService courseService;
    private final PresentationConversionService presentationConversionService;
    
    public LessonService(LessonRepository lessonRepository, 
                         CourseService courseService, 
                         PresentationConversionService presentationConversionService) {
        this.lessonRepository = lessonRepository;
        this.courseService = courseService;
        this.presentationConversionService = presentationConversionService;
    }
    
    public Lesson getLessonById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Lesson not found with ID: " + id));
    }
    
    @Transactional
    public Lesson createLesson(Lesson lesson, Long courseId) {
        Course course = courseService.getCourseById(courseId);
        lesson.setCourse(course);
        return lessonRepository.save(lesson);
    }
    
    @Transactional
    public Lesson updateLesson(Long id, Lesson updatedLesson) {
        Lesson existingLesson = getLessonById(id);
        
        existingLesson.setTitle(updatedLesson.getTitle());
        existingLesson.setContent(updatedLesson.getContent());
        existingLesson.setVideoUrl(updatedLesson.getVideoUrl());
        existingLesson.setVideoType(updatedLesson.getVideoType());
        existingLesson.setPdfUrl(updatedLesson.getPdfUrl());
        existingLesson.setPdfName(updatedLesson.getPdfName());
        existingLesson.setPresentationUrl(updatedLesson.getPresentationUrl());
        existingLesson.setPresentationName(updatedLesson.getPresentationName());
        existingLesson.setOrderInCourse(updatedLesson.getOrderInCourse());
        
        return lessonRepository.save(existingLesson);
    }
    
    @Transactional
    public void deleteLesson(Long id) {
        Lesson lesson = getLessonById(id);
        lessonRepository.delete(lesson);
    }
    
    @Transactional
    public Lesson uploadPresentation(Long lessonId, MultipartFile presentationFile) throws IOException {
        Lesson lesson = getLessonById(lessonId);
        
        // Convert file to base64 for storage
        String base64Presentation = "data:application/vnd.openxmlformats-officedocument.presentationml.presentation;base64," + 
                                   Base64.getEncoder().encodeToString(presentationFile.getBytes());
        lesson.setPresentationUrl(base64Presentation);
        lesson.setPresentationName(presentationFile.getOriginalFilename());
        
        // Reset conversion status when a new presentation is uploaded
        lesson.setPresentationConversionStatus(null);
        lesson.setPresentationHtmlContent(null);
        lesson.setConvertedPresentationUrl(null);
        
        Lesson savedLesson = lessonRepository.save(lesson);
        
        // Trigger conversion process asynchronously
        presentationConversionService.convertPresentation(savedLesson.getId());
        
        return savedLesson;
    }
    
    /**
     * Get the HTML content of a converted presentation
     * @param lessonId The lesson ID
     * @return The HTML content or null if not converted
     */
    public String getConvertedPresentationHtml(Long lessonId) {
        Lesson lesson = getLessonById(lessonId);
        
        if (lesson.getPresentationConversionStatus() == null || 
            !lesson.getPresentationConversionStatus().equals("COMPLETED")) {
            return null;
        }
        
        return lesson.getPresentationHtmlContent();
    }
    
    /**
     * Check if a presentation has been converted successfully
     * @param lessonId The lesson ID
     * @return true if converted, false otherwise
     */
    public boolean isPresentationConverted(Long lessonId) {
        Lesson lesson = getLessonById(lessonId);
        return lesson.getPresentationConversionStatus() != null && 
               lesson.getPresentationConversionStatus().equals("COMPLETED");
    }
    
    /**
     * Get the status of presentation conversion
     * @param lessonId The lesson ID
     * @return The status ("PENDING", "COMPLETED", "FAILED", or null)
     */
    public String getPresentationConversionStatus(Long lessonId) {
        Lesson lesson = getLessonById(lessonId);
        return lesson.getPresentationConversionStatus();
    }
    
    /**
     * Trigger conversion for an existing presentation
     * @param lessonId The lesson ID
     * @return true if conversion started, false if no presentation exists
     */
    public boolean triggerPresentationConversion(Long lessonId) {
        Lesson lesson = getLessonById(lessonId);
        
        if (lesson.getPresentationUrl() == null || lesson.getPresentationUrl().isEmpty()) {
            return false;
        }
        
        presentationConversionService.convertPresentation(lessonId);
        return true;
    }
}
