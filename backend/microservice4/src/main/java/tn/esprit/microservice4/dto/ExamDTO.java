package tn.esprit.microservice4.dto;

import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;

public class ExamDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime examDate;
    private Long userId;
    private MultipartFile examFile;
    private String examFileUrl;
    private String submittedFileUrl;
    private Float score;
    private String status;
    private boolean certificateGenerated;
    private String certificateUrl;

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getExamDate() {
        return examDate;
    }

    public Long getUserId() {
        return userId;
    }

    public MultipartFile getExamFile() {
        return examFile;
    }

    public String getExamFileUrl() {
        return examFileUrl;
    }

    public String getSubmittedFileUrl() {
        return submittedFileUrl;
    }

    public Float getScore() {
        return score;
    }

    public String getStatus() {
        return status;
    }

    public boolean isCertificateGenerated() {
        return certificateGenerated;
    }

    public String getCertificateUrl() {
        return certificateUrl;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExamDate(LocalDateTime examDate) {
        this.examDate = examDate;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setExamFile(MultipartFile examFile) {
        this.examFile = examFile;
    }

    public void setExamFileUrl(String examFileUrl) {
        this.examFileUrl = examFileUrl;
    }

    public void setSubmittedFileUrl(String submittedFileUrl) {
        this.submittedFileUrl = submittedFileUrl;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCertificateGenerated(boolean certificateGenerated) {
        this.certificateGenerated = certificateGenerated;
    }

    public void setCertificateUrl(String certificateUrl) {
        this.certificateUrl = certificateUrl;
    }
}