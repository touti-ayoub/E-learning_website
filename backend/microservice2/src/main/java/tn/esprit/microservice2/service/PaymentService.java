package tn.esprit.microservice2.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.microservice2.Model.*;
import tn.esprit.microservice2.repo.IFactureRepository;
import tn.esprit.microservice2.repo.IPaymentRepository;
import tn.esprit.microservice2.repo.IPaymentScheduleRepository;
import tn.esprit.microservice2.repo.ISubscriptionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private IPaymentRepository paymentRepository;

    @Autowired
    private ISubscriptionRepository subscriptionRepository;

    @Autowired
    private IPaymentScheduleRepository paymentScheduleRepository;

    @Autowired
    private IFactureRepository invoiceRepository;

    // Créer un paiement pour un abonnement
    @Transactional
    public Payment createPayment(Long subscriptionId, PaymentType paymentType,int insattlement) {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findById(subscriptionId);
        if (subscriptionOpt.isPresent()) {
            Subscription subscription = subscriptionOpt.get();

            // Créer le paiement (full ou échelonné)
            Payment payment = new Payment();
            payment.setSubscription(subscription);
            payment.setAmount(subscription.getCourse().getPrice());
            payment.setCurrency("EUR");
            payment.setPaymentMethod("Credit Card");  // Exemple
            payment.setStatus(PaymentStatus.PENDING);
            payment.setPaymentDate(LocalDate.now().atStartOfDay());
            payment.setDueDate(subscription.getEndDate());
            payment = paymentRepository.save(payment);

            // Créer les échéances si paiement par échelonnement
            if (paymentType == PaymentType.INSTALLMENTS) {
                createInstallments(payment,insattlement);
            }

            return payment;
        }
        throw new RuntimeException("Subscription not found");
    }

    // Créer des échéances pour le paiement par échelonnement
    private void createInstallments(Payment payment,int insattlement) {
        Subscription subscription = payment.getSubscription();
        double totalAmount = subscription.getCourse().getPrice();
        int numInstallments = insattlement; // Exemple, le nombre d'échéances peut être dynamique

        double installmentAmount = totalAmount / numInstallments;
        LocalDate currentDate = LocalDate.now();

        for (int i = 1; i <= numInstallments; i++) {
            PaymentSchedule schedule = new PaymentSchedule();
            schedule.setPayment(payment);
            schedule.setInstallmentNumber(i);
            schedule.setAmount(installmentAmount);
            schedule.setDueDate(currentDate.plusMonths(i));
            schedule.setStatus(PaymentScheduleStatus.PENDING);
            paymentScheduleRepository.save(schedule);
        }
    }

    // Mettre à jour l'état de paiement (par exemple, quand le paiement est effectué)
    @Transactional
    public Payment updatePaymentStatus(Long paymentId, PaymentStatus status) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus(status);
            return paymentRepository.save(payment);
        }
        throw new RuntimeException("Payment not found");
    }

    // Mettre à jour l'état d'une échéance (ex : payé ou en retard)
    @Transactional
    public PaymentSchedule updatePaymentScheduleStatus(Long scheduleId, PaymentScheduleStatus status) {
        Optional<PaymentSchedule> scheduleOpt = paymentScheduleRepository.findById(scheduleId);
        if (scheduleOpt.isPresent()) {
            PaymentSchedule schedule = scheduleOpt.get();
            schedule.setStatus(status);
            return paymentScheduleRepository.save(schedule);
        }
        throw new RuntimeException("Payment schedule not found");
    }

    // Récupérer les paiements d'un abonnement
    public List<Payment> getPaymentsBySubscription(Long subscriptionId) {
        return paymentRepository.findBySubscriptionId(subscriptionId);
    }
}
