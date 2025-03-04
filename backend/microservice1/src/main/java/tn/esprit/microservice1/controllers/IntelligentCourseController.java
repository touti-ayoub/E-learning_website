package tn.esprit.microservice1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice1.services.IntelligentCourseService;
import tn.esprit.microservice1.entities.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/intelligent")
@CrossOrigin(origins = "*")
public class IntelligentCourseController {

    @Autowired
    private IntelligentCourseService intelligentCourseService;

    @GetMapping("/recommendations/{userId}")
    public ResponseEntity<List<Course>> getPersonalizedRecommendations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(intelligentCourseService.getPersonalizedRecommendations(userId, limit));
    }

    @PostMapping("/courses/{courseId}/enrich")
    public ResponseEntity<Void> enrichCourseWithAI(@PathVariable Long courseId) {
        // TODO: Get course by ID and enrich
        return ResponseEntity.ok().build();
    }

    @GetMapping("/courses/{courseId}/learning-path/{userId}")
    public ResponseEntity<List<LearningObjective>> getAdaptiveLearningPath(
            @PathVariable Long courseId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(intelligentCourseService.generateAdaptiveLearningPath(userId, courseId));
    }

    @GetMapping("/courses/{courseId}/knowledge-gaps/{userId}")
    public ResponseEntity<Map<String, Double>> getKnowledgeGaps(
            @PathVariable Long courseId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(intelligentCourseService.identifyKnowledgeGaps(userId, courseId));
    }

    @GetMapping("/courses/{courseId}/practice-questions/{userId}")
    public ResponseEntity<List<String>> getPersonalizedQuestions(
            @PathVariable Long courseId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(intelligentCourseService.generatePersonalizedQuestions(userId, courseId));
    }

    @GetMapping("/courses/{courseId}/engagement/{userId}")
    public ResponseEntity<Map<String, Object>> getEngagementAnalysis(
            @PathVariable Long courseId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(intelligentCourseService.analyzeEngagementPatterns(userId, courseId));
    }

    @GetMapping("/courses/{courseId}/performance-prediction/{userId}")
    public ResponseEntity<Map<String, Double>> getPredictedPerformance(
            @PathVariable Long courseId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(intelligentCourseService.predictPerformance(userId, courseId));
    }
} 