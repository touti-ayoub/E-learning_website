package tn.esprit.microservice4.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.microservice4.dto.ExamDTO;
import tn.esprit.microservice4.entities.Certificate;
import tn.esprit.microservice4.entities.Exam;
import tn.esprit.microservice4.repositories.ExamRepository;

import java.util.List;

@Service
public class ExamService {

    private final ExamRepository examRepo;
    private final CertificateService certificateService;
    private final MailService mailService;
    private final FileStorageService fileStorageService;

    public ExamService(ExamRepository examRepo,
                       CertificateService certificateService,
                       MailService mailService,
                       FileStorageService fileStorageService) {
        this.examRepo = examRepo;
        this.certificateService = certificateService;
        this.mailService = mailService;
        this.fileStorageService = fileStorageService;
    }

    public Exam createExam(ExamDTO dto, MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("Le fichier de l'examen est requis");
            }

            Exam exam = new Exam();
            exam.setTitle(dto.getTitle());
            exam.setDescription(dto.getDescription());
            exam.setDate(dto.getExamDate());
            exam.setUserId(dto.getUserId());
            exam.setStatus(Exam.ExamStatus.CREATED);

            String fileUrl = fileStorageService.saveFile(file);
            exam.setExamFileUrl(fileUrl);

            return examRepo.save(exam);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création de l'examen: " + e.getMessage(), e);
        }
    }

    public Exam submitExam(Long examId, MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("Le fichier de rendu est requis");
            }

            Exam exam = examRepo.findById(examId)
                    .orElseThrow(() -> new IllegalArgumentException("Examen non trouvé"));

            if (exam.getStatus() != Exam.ExamStatus.CREATED) {
                throw new IllegalStateException("L'examen a déjà été soumis ou noté");
            }

            String fileUrl = fileStorageService.saveFile(file);
            exam.setSubmittedFileUrl(fileUrl);
            exam.setStatus(Exam.ExamStatus.SUBMITTED);

            return examRepo.save(exam);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la soumission de l'examen: " + e.getMessage(), e);
        }
    }

    public Exam assignScore(Long examId, Double score) {
        try {
            if (score == null || score < 0 || score > 20) {
                throw new IllegalArgumentException("La note doit être comprise entre 0 et 20");
            }

            Exam exam = examRepo.findById(examId)
                    .orElseThrow(() -> new IllegalArgumentException("Examen non trouvé"));

            if (exam.getStatus() != Exam.ExamStatus.SUBMITTED) {
                throw new IllegalStateException("L'examen doit être soumis avant d'être noté");
            }

            exam.setScore(score);
            exam.setPassed(score >= 10.0);
            exam.setStatus(score >= 10.0 ? Exam.ExamStatus.PASSED : Exam.ExamStatus.FAILED);

            if (exam.getPassed()) {
                Certificate cert = certificateService.generateCertificate(exam);
                exam.setCertificate(cert);
                exam.setCertificateGenerated(true);
                exam.setCertificateUrl(cert.getCertificateUrl());
                mailService.sendCertificateNotification(exam.getUserId(), cert);
            }

            return examRepo.save(exam);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'attribution de la note: " + e.getMessage(), e);
        }
    }

    public List<Exam> getExamsByUser(Long userId) {
        return examRepo.findByUserId(userId);
    }

    public List<Exam> getAllExams() {
        return examRepo.findAll();
    }
}