package tn.esprit.microservice4.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.microservice4.dto.ExamDTO;
import tn.esprit.microservice4.dto.ScoreDTO;
import tn.esprit.microservice4.entities.Exam;
import tn.esprit.microservice4.services.ExamService;
import tn.esprit.microservice4.services.FileStorageService;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class ExamController {
    private static final Logger logger = LoggerFactory.getLogger(ExamController.class);

    private final ExamService examService;
    private final FileStorageService fileStorageService;

    public ExamController(ExamService examService, FileStorageService fileStorageService) {
        this.examService = examService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    public ResponseEntity<?> createExam(
            @RequestPart("exam") ExamDTO dto,
            @RequestPart("file") MultipartFile file) {
        try {
            Exam exam = examService.createExam(dto, file);
            return ResponseEntity.ok(exam);
        } catch (Exception e) {
            logger.error("Error creating exam", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating exam: " + e.getMessage());
        }
    }

    @PostMapping("/{examId}/submit")
    public ResponseEntity<?> submitExam(
            @PathVariable Long examId,
            @RequestParam("file") MultipartFile file) {
        try {
            Exam exam = examService.submitExam(examId, file);
            return ResponseEntity.ok(exam);
        } catch (Exception e) {
            logger.error("Error submitting exam", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error submitting exam: " + e.getMessage());
        }
    }

    @PostMapping("/{examId}/score")
    public ResponseEntity<?> assignScore(@PathVariable Long examId, @RequestBody ScoreDTO dto) {
        try {
            Exam exam = examService.assignScore(examId, dto.getScore());
            return ResponseEntity.ok(exam);
        } catch (Exception e) {
            logger.error("Error assigning score", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error assigning score: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getExamsForUser(@PathVariable Long userId) {
        try {
            List<Exam> exams = examService.getExamsByUser(userId);
            return ResponseEntity.ok(exams);
        } catch (Exception e) {
            logger.error("Error getting exams for user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting exams: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllExams() {
        try {
            List<Exam> exams = examService.getAllExams();
            return ResponseEntity.ok(exams);
        } catch (Exception e) {
            logger.error("Error getting all exams", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting exams: " + e.getMessage());
        }
    }

    @GetMapping("/{examId}")
    public ResponseEntity<?> getExamById(@PathVariable Long examId) {
        try {
            System.out.println("Tentative de récupération de l'examen avec l'ID: " + examId);
            Exam exam = examService.getExamById(examId);
            System.out.println("Examen trouvé: " + exam);
            return ResponseEntity.ok(exam);
        } catch (IllegalArgumentException e) {
            System.out.println("Examen non trouvé: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Examen non trouvé avec l'ID: " + examId);
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération de l'examen: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération de l'examen: " + e.getMessage());
        }
    }

    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<?> downloadFile(@PathVariable String filename) {
        try {
            logger.info("Attempting to download file: {}", filename);
            Resource resource = fileStorageService.downloadFile(filename);

            String contentType = "application/pdf";
            String originalFilename = resource.getFilename();
            logger.info("File found: {}", originalFilename);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + originalFilename + "\"")
                    .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                    .body(resource);
        } catch (Exception e) {
            logger.error("Error downloading file: {}", filename, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error downloading file: " + e.getMessage());
        }
    }
}