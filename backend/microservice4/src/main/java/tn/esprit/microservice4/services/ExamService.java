package tn.esprit.microservice4.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.microservice4.entities.Certificate;
import tn.esprit.microservice4.entities.Exam;
import tn.esprit.microservice4.entities.Exam.ExamStatus;
import tn.esprit.microservice4.repositories.ExamRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ExamService {
    private static final Logger logger = LoggerFactory.getLogger(ExamService.class);

    private final ExamRepository examRepository;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;
    private final CertificateService certificateService;

    @Autowired
    public ExamService(
            ExamRepository examRepository,
            FileStorageService fileStorageService,
            ObjectMapper objectMapper,
            CertificateService certificateService) {
        this.examRepository = examRepository;
        this.fileStorageService = fileStorageService;
        this.objectMapper = objectMapper;
        this.certificateService = certificateService;
    }

    public Exam createExam(String examJson, MultipartFile file) throws Exception {
        try {
            logger.info("Creating new exam");
            // Parse the exam JSON
            Exam exam = objectMapper.readValue(examJson, Exam.class);

            // Store the file and get its URL
            String fileUrl = fileStorageService.saveFile(file);

            // Set the file URL and other properties
            exam.setExamFileUrl(fileUrl);
            exam.setDate(LocalDateTime.now());
            exam.setStatus(ExamStatus.CREATED);

            // Save the exam
            Exam savedExam = examRepository.save(exam);
            logger.info("Created exam with ID: {}", savedExam.getId());
            return savedExam;
        } catch (Exception e) {
            logger.error("Error creating exam: {}", e.getMessage(), e);
            throw new Exception("Error creating exam: " + e.getMessage());
        }
    }

    public Exam submitExam(Long examId, MultipartFile file) throws Exception {
        logger.info("Submitting exam with ID: {}", examId);
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new Exception("Exam not found"));

        if (exam.getStatus() != ExamStatus.CREATED) {
            throw new Exception("Exam cannot be submitted at this stage");
        }

        String fileUrl = fileStorageService.saveFile(file);
        exam.setSubmittedFileUrl(fileUrl);
        exam.setStatus(ExamStatus.SUBMITTED);
        Exam savedExam = examRepository.save(exam);
        logger.info("Successfully submitted exam with ID: {}", examId);
        return savedExam;
    }

    public Exam gradeExam(Long examId, Double score) throws Exception {
        logger.info("Grading exam with ID: {}, score: {}", examId, score);
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new Exception("Exam not found"));

        if (exam.getStatus() != ExamStatus.SUBMITTED) {
            throw new Exception("Exam must be submitted before grading");
        }

        exam.setScore(score);
        boolean passed = score >= 70.0;
        exam.setPassed(passed);
        exam.setStatus(passed ? ExamStatus.PASSED : ExamStatus.FAILED);

        Exam savedExam = examRepository.save(exam);

        // Generate certificate if exam is passed
        if (passed) {
            try {
                logger.info("Generating certificate for passed exam ID: {}", examId);
                certificateService.generateCertificate(examId);
            } catch (Exception e) {
                logger.error("Error generating certificate: {}", e.getMessage(), e);
                // Don't fail the grading process if certificate generation fails
            }
        }

        logger.info("Successfully graded exam with ID: {}", examId);
        return savedExam;
    }

    public byte[] getCertificateFile(Long examId) throws Exception {
        try {
            logger.info("Getting certificate file for exam ID: {}", examId);
            return certificateService.getCertificateByExamId(examId);
        } catch (Exception e) {
            logger.error("Error getting certificate file: {}", e.getMessage(), e);
            throw new Exception("Error getting certificate file: " + e.getMessage());
        }
    }

    public Optional<Exam> getExamById(Long id) {
        return examRepository.findById(id);
    }

    public List<Exam> getExamsByUserId(Long userId) {
        return examRepository.findByUserId(userId);
    }

    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    public void deleteExam(Long id) {
        examRepository.deleteById(id);
    }
}