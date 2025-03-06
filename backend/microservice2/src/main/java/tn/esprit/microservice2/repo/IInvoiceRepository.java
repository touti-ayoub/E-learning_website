package tn.esprit.microservice2.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microservice2.Model.Invoice;

import java.util.Optional;

public interface IInvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByPaymentId(Long paymentId);

    /**
     * Check if an invoice exists for a payment
     * @param paymentId The payment ID to check
     * @return true if an invoice exists, false otherwise
     */
    boolean existsByPaymentId(Long paymentId);

    /**
     * Check if an invoice exists for a specific installment
     * @param paymentId The payment ID
     * @param installmentNumber The installment number
     * @return true if an invoice exists, false otherwise
     */
    boolean existsByPaymentIdAndInstallmentNumber(Long paymentId, Integer installmentNumber);

    /**
     * Find an invoice for a specific installment
     * @param paymentId The payment ID
     * @param installmentNumber The installment number
     * @return The invoice if found
     */
    Optional<Invoice> findByPaymentIdAndInstallmentNumber(Long paymentId, Integer installmentNumber);
    //long countByYearMonth(String yearMonth);
}
