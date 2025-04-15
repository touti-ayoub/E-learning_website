package tn.esprit.microservice1.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice1.entities.Lesson;
import tn.esprit.microservice1.repositories.LessonRepository;
import tn.esprit.microservice1.services.PresentationConversionService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/presentations")
public class PresentationController {

    private final PresentationConversionService presentationConversionService;
    private final LessonRepository lessonRepository;

    public PresentationController(PresentationConversionService presentationConversionService, 
                                 LessonRepository lessonRepository) {
        this.presentationConversionService = presentationConversionService;
        this.lessonRepository = lessonRepository;
    }

    @PostMapping("/convert/{lessonId}")
    public ResponseEntity<Map<String, Object>> convertPresentation(@PathVariable Long lessonId) {
        Map<String, Object> response = new HashMap<>();
        
        // Check if lesson exists
        if (!lessonRepository.existsById(lessonId)) {
            response.put("success", false);
            response.put("message", "Lesson not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        // Start conversion process asynchronously
        presentationConversionService.convertPresentation(lessonId);
        
        response.put("success", true);
        response.put("message", "Conversion started");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/status/{lessonId}")
    public ResponseEntity<Map<String, Object>> getConversionStatus(@PathVariable Long lessonId) {
        Map<String, Object> response = new HashMap<>();
        
        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
        
        if (lesson == null) {
            response.put("success", false);
            response.put("message", "Lesson not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        response.put("success", true);
        response.put("lessonId", lessonId);
        response.put("status", lesson.getPresentationConversionStatus());
        
        if (lesson.getPresentationConversionStatus() != null && 
            lesson.getPresentationConversionStatus().equals("COMPLETED")) {
            response.put("htmlContent", lesson.getPresentationHtmlContent());
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/html/{lessonId}")
    public ResponseEntity<String> getConvertedHtml(@PathVariable Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
        
        if (lesson == null) {
            return ResponseEntity.notFound().build();
        }
        
        if (lesson.getPresentationConversionStatus() == null || 
            !lesson.getPresentationConversionStatus().equals("COMPLETED") || 
            lesson.getPresentationHtmlContent() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Presentation not converted yet");
        }
        
        return ResponseEntity.ok()
                .header("Content-Type", "text/html")
                .body(lesson.getPresentationHtmlContent());
    }
} 