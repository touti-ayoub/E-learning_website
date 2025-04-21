package tn.esprit.microservice4.services;

import org.springframework.stereotype.Service;
import tn.esprit.microservice4.entities.Certificate;
import tn.esprit.microservice4.entities.Exam;
import tn.esprit.microservice4.repositories.CertificateRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepo;

    public CertificateService(CertificateRepository certificateRepo) {
        this.certificateRepo = certificateRepo;
    }

    public Certificate generateCertificate(Exam exam) {
        Certificate cert = new Certificate();
        cert.setExam(exam);
        cert.setIssuedDate(LocalDateTime.now());
        cert.setCertificateUrl("https://yourdomain.com/certificates/" + UUID.randomUUID());
        return certificateRepo.save(cert);
    }
}
