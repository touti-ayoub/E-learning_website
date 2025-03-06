package tn.esprit.microservice2.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.microservice2.Model.Payment;
import tn.esprit.microservice2.Model.PaymentStatus;

import java.util.List;
import java.util.Optional;

public interface IPaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findBySubscriptionId(Long subscriptionId);
    @Query("SELECT DISTINCT p FROM Payment p " +
            "LEFT JOIN FETCH p.subscription s " +
            "LEFT JOIN FETCH s.user " +
            "LEFT JOIN FETCH s.course " +
            "LEFT JOIN FETCH p.paymentSchedules " +
            "LEFT JOIN FETCH p.invoice " +
            "WHERE s.id = :subscriptionId")
    List<Payment> findBySubscriptionIdWithDetails(@Param("subscriptionId") Long subscriptionId);

    @Query("SELECT p FROM Payment p " +
            "LEFT JOIN FETCH p.subscription s " +
            "LEFT JOIN FETCH s.user " +
            "LEFT JOIN FETCH s.course " +
            "LEFT JOIN FETCH p.paymentSchedules " +
            "LEFT JOIN FETCH p.invoice " +
            "WHERE p.id = :id")
    Optional<Payment> findByIdWithDetails(@Param("id") Long id);
    long countByStatus(PaymentStatus status);

    // Add this method
    @Query("SELECT COUNT(p) FROM Payment p JOIN p.subscription s JOIN s.user u WHERE u.id = :userId")
    int countPaymentsByUserId(@Param("userId") Long userId);}