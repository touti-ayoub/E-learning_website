package tn.esprit.microservice2.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microservice2.Model.PaymentSchedule;

public interface IPaymentScheduleRepository extends JpaRepository<PaymentSchedule, Long> {
}
