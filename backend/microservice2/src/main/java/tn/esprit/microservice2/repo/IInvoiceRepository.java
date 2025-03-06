package tn.esprit.microservice2.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microservice2.Model.Invoice;

import java.util.Optional;

public interface IInvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByPaymentId(Long paymentId);


    //long countByYearMonth(String yearMonth);
}
