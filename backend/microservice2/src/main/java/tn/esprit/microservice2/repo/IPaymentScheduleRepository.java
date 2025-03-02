package tn.esprit.microservice2.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.microservice2.Model.Payment;
import tn.esprit.microservice2.Model.PaymentSchedule;
import tn.esprit.microservice2.Model.PaymentScheduleStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface IPaymentScheduleRepository extends JpaRepository<PaymentSchedule, Long> {

    List<PaymentSchedule> findByPayment(Payment payment);

    List<PaymentSchedule> findByPaymentId(Long paymentId);

    @Query("SELECT ps FROM PaymentSchedule ps " +
            "JOIN ps.payment p " +
            "JOIN p.subscription s " +
            "JOIN s.user u " +
            "WHERE u.id = :userId AND (ps.status = 'PENDING' OR ps.status = 'OVERDUE') " +
            "ORDER BY ps.dueDate ASC")
    List<PaymentSchedule> findPendingInstallmentsByUserId(@Param("userId") Long userId, @Param("status") PaymentScheduleStatus status);

    // Option 2: Alternative native query to diagnose issues
    @Query(value = "SELECT ps.* FROM payment_schedule ps " +
            "JOIN payment p ON ps.payment_id = p.id " +
            "JOIN subscription s ON p.subscription_id = s.id " +
            "JOIN user u ON s.user_id = u.id " +
            "WHERE u.id = :userId AND ps.status IN ('PENDING', 'OVERDUE') " +
            "ORDER BY ps.due_date ASC", nativeQuery = true)
    List<PaymentSchedule> findPendingInstallmentsNative(@Param("userId") Long userId);
    
    List<PaymentSchedule> findByPaymentAndDueDateBefore(Payment payment, LocalDateTime dueDate);

    // New methods for the scheduler
    List<PaymentSchedule> findByStatusAndDueDateBefore(PaymentScheduleStatus status, LocalDate dueDate);

    List<PaymentSchedule> findByStatusAndDueDateBetween(PaymentScheduleStatus status, LocalDate startDate, LocalDate endDate);

    // Count methods
    long countByStatus(PaymentScheduleStatus status);

    List<PaymentSchedule> findByStatus(PaymentScheduleStatus paymentScheduleStatus);

    PaymentSchedule findFirstByPaymentAndStatusInOrderByDueDateAsc(Payment payment, List<PaymentScheduleStatus> statuses);
}