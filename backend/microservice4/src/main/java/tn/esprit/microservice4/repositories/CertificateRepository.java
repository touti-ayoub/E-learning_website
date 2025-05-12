package tn.esprit.microservice4.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.microservice4.entities.Certificate;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByExam_Id(Long examId);

    // Méthodes personnalisées si nécessaire
}
