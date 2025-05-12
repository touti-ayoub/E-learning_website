package tn.esprit.microservice4.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.microservice4.client.UserClient;
import tn.esprit.microservice4.entities.Exam;
import tn.esprit.microservice4.services.ExamService;
import tn.esprit.microservice4.services.FileStorageService;
import tn.esprit.microservice4.dto.UserDTO;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class ExamController {
    private static final Logger logger = LoggerFactory.getLogger(ExamController.class);

    @Autowired
    private ExamService examService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UserClient userClient;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createExam(
            @RequestPart("exam") String examJson,
            @RequestPart("file") MultipartFile file) {
        try {
            logger.info("Received request to create exam");
            Exam exam = examService.createExam(examJson, file);
            return ResponseEntity.ok(exam);
        } catch (Exception e) {
            logger.error("Error creating exam: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating exam: " + e.getMessage());
        }
    }

    @PostMapping("/{examId}/submit")
    public ResponseEntity<?> submitExam(
            @PathVariable Long examId,
            @RequestParam("file") MultipartFile file) {
        try {
            logger.info("Received request to submit exam ID: {}", examId);
            Exam exam = examService.submitExam(examId, file);
            return ResponseEntity.ok(exam);
        } catch (Exception e) {
            logger.error("Error submitting exam: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error submitting exam: " + e.getMessage());
        }
    }

    @PostMapping("/{examId}/grade")
    public ResponseEntity<?> gradeExam(
            @PathVariable Long examId,
            @RequestParam Double score) {
        try {
            logger.info("Received request to grade exam ID: {} with score: {}", examId, score);
            Exam exam = examService.gradeExam(examId, score);
            return ResponseEntity.ok(exam);
        } catch (Exception e) {
            logger.error("Error grading exam: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error grading exam: " + e.getMessage());
        }
    }

    @GetMapping("/{examId}/certificate")
    public ResponseEntity<ByteArrayResource> downloadCertificate(@PathVariable Long examId) {
        try {
            logger.info("Received request to download certificate for exam ID: {}", examId);
            byte[] certificateBytes = examService.getCertificateFile(examId);
            ByteArrayResource resource = new ByteArrayResource(certificateBytes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificate.pdf");
            headers.add("Access-Control-Expose-Headers", "Content-Disposition");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(certificateBytes.length)
                    .body(resource);
        } catch (Exception e) {
            logger.error("Error downloading certificate: {}", e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Exam>> getExamsByUserId(@PathVariable Long userId) {
        logger.info("Received request to get exams for user ID: {}", userId);
        return ResponseEntity.ok(examService.getExamsByUserId(userId));
    }

    @GetMapping
    public ResponseEntity<List<Exam>> getAllExams() {
        logger.info("Received request to get all exams");
        return ResponseEntity.ok(examService.getAllExams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExamById(@PathVariable Long id) {
        logger.info("Received request to get exam with ID: {}", id);
        return examService.getExamById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExam(@PathVariable Long id) {
        try {
            logger.info("Received request to delete exam with ID: {}", id);
            examService.deleteExam(id);
            return ResponseEntity.ok().body("Exam deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting exam: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting exam: " + e.getMessage());
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        try {
            List<UserDTO> users = userClient.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}