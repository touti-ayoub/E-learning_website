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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface IPaymentScheduleRepository extends JpaRepository<PaymentSchedule, Long> ,
        JpaSpecificationExecutor<PaymentSchedule>{

    List<PaymentSchedule> findByPayment(Payment payment);

    List<PaymentSchedule> findByPaymentId(Long paymentId);

    @Query("SELECT ps FROM PaymentSchedule ps " +
            "JOIN ps.payment p " +
            "JOIN p.subscription s " +
            "JOIN s.user u " +
            "WHERE u.id = :userId AND (ps.status = 'PENDING' OR ps.status = 'OVERDUE') " +
            "ORDER BY ps.dueDate ASC")
    List<PaymentSchedule> findPendingInstallmentsByUserId(@Param("userId") Long userId, @Param("status") PaymentScheduleStatus status);
    
    List<PaymentSchedule> findByPaymentAndDueDateBefore(Payment payment, LocalDate dueDate);

    // New methods for the scheduler
    List<PaymentSchedule> findByStatusAndDueDateBefore(PaymentScheduleStatus status, LocalDate dueDate);

    List<PaymentSchedule> findByStatusAndDueDateBetween(PaymentScheduleStatus status, LocalDate startDate, LocalDate endDate);

    // Count methods
    long countByStatus(PaymentScheduleStatus status);

    PaymentSchedule findFirstByPaymentAndStatusInOrderByDueDateAsc(Payment payment, List<PaymentScheduleStatus> statuses);

    List<PaymentSchedule> findByStatusAndDueDate(PaymentScheduleStatus status, LocalDate dueDate);
    @Query("SELECT ps FROM PaymentSchedule ps " +
            "JOIN ps.payment p " +
            "JOIN p.subscription s " +
            "JOIN s.user u " +
            "WHERE u.id = :userId AND ps.status = :status " +
            "AND ps.dueDate BETWEEN :startDate AND :endDate " +
            "ORDER BY ps.dueDate ASC")
    List<PaymentSchedule> findUpcomingInstallmentsByUserId(
            @Param("userId") Long userId,
            @Param("status") PaymentScheduleStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    List<PaymentSchedule> findByStatus(PaymentScheduleStatus status);


    @Query("SELECT ps FROM PaymentSchedule ps " +
            "JOIN FETCH ps.payment p " +
            "JOIN FETCH p.subscription s " +
            "JOIN FETCH s.user u " +
            "JOIN FETCH s.course c " +
            "WHERE ps.status = :status AND ps.dueDate = :dueDate")
    List<PaymentSchedule> findByStatusAndDueDateWithDetails(
            @Param("status") PaymentScheduleStatus status,
            @Param("dueDate") LocalDate dueDate);

    List<PaymentSchedule> findAll(Specification<PaymentSchedule> spec);

    // Add this method to your PaymentScheduleRepository
    long countByStatusAndDueDateBefore(PaymentScheduleStatus status, LocalDate date);

}