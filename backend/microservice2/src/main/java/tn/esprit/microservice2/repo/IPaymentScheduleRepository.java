package tn.esprit.microservice2.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microservice2.Model.Payment;
import tn.esprit.microservice2.Model.PaymentSchedule;

import java.time.LocalDateTime;
import java.util.List;


public interface IPaymentScheduleRepository extends JpaRepository<PaymentSchedule, Long> {
    //List<PaymentSchedule> findUnpaidByUserId(Long userId);
    List<PaymentSchedule> findByPayment(Payment payment);
    List<PaymentSchedule> findByPaymentId(Long paymentId);
    List<PaymentSchedule> findByPaymentAndDueDateBefore(Payment payment, LocalDateTime date);

}
