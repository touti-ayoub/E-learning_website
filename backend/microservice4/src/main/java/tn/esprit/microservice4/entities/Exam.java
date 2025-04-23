package tn.esprit.microservice4.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class Exam {
    public enum ExamStatus {
        CREATED,
        SUBMITTED,
        GRADED,
        PASSED,
        FAILED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private LocalDateTime date;
    private Long userId;
    private Double score;
    private Boolean passed;

    private String examFileUrl;
    private String submittedFileUrl;

    @Enumerated(EnumType.STRING)
    private ExamStatus status = ExamStatus.CREATED;

    @OneToOne(mappedBy = "exam", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Certificate certificate;

    // Certificate related fields
    private boolean certificateGenerated;
    private String certificateUrl;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public Boolean getPassed() { return passed; }
    public void setPassed(Boolean passed) { this.passed = passed; }

    public String getSubmittedFileUrl() { return submittedFileUrl; }
    public void setSubmittedFileUrl(String submittedFileUrl) { this.submittedFileUrl = submittedFileUrl; }

    public String getExamFileUrl() {
        return examFileUrl;
    }

    public void setExamFileUrl(String examFileUrl) {
        this.examFileUrl = examFileUrl;
    }

    public ExamStatus getStatus() { return status; }
    public void setStatus(ExamStatus status) { this.status = status; }

    public Certificate getCertificate() { return certificate; }
    public void setCertificate(Certificate certificate) { this.certificate = certificate; }

    public boolean isCertificateGenerated() { return certificateGenerated; }
    public void setCertificateGenerated(boolean certificateGenerated) { this.certificateGenerated = certificateGenerated; }

    public String getCertificateUrl() { return certificateUrl; }
    public void setCertificateUrl(String certificateUrl) { this.certificateUrl = certificateUrl; }
}