package tn.esprit.microservice2.Rest;


import org.springframework.http.ResponseEntity;
import tn.esprit.microservice2.Model.*;
import tn.esprit.microservice2.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mic2/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Créer un paiement pour un abonnement
   /* @PostMapping("/create/{subscriptionId}")
    public Payment createPayment(@PathVariable Long subscriptionId,@PathVariable int insattlement, @RequestBody PaymentType paymentType) {
        return paymentService.createPayment(subscriptionId, paymentType,insattlement);
    }

    // Mettre à jour l'état du paiement
    @PutMapping("/update/{paymentId}")
    public Payment updatePaymentStatus(@PathVariable Long paymentId, @RequestBody PaymentStatus status) {
        return paymentService.updatePaymentStatus(paymentId, status);
    }

    // Mettre à jour l'état de l'échéance
    @PutMapping("/schedule/update/{scheduleId}")
    public PaymentSchedule updatePaymentScheduleStatus(@PathVariable Long scheduleId, @RequestBody PaymentScheduleStatus status) {
        return paymentService.updatePaymentScheduleStatus(scheduleId, status);
    }

    // Récupérer les paiements par abonnement
    @GetMapping("/bySubscription/{subscriptionId}")
    public List<Payment> getPaymentsBySubscription(@PathVariable Long subscriptionId) {
        return paymentService.getPaymentsBySubscription(subscriptionId);
    }*/


    @PostMapping("/subscription/{subscriptionId}")
    public ResponseEntity<Payment> createPayment(
            @PathVariable Long subscriptionId,
            @RequestParam(defaultValue = "3") int installments) {
        try {
            Payment payment = paymentService.createPayment(subscriptionId, installments);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{paymentId}/status")
    public ResponseEntity<Payment> updatePaymentStatus(
            @PathVariable Long paymentId,
            @RequestBody PaymentStatus status) {
        try {
            Payment payment = paymentService.updatePaymentStatus(paymentId, status);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/schedules/{scheduleId}/status")
    public ResponseEntity<PaymentSchedule> updateScheduleStatus(
            @PathVariable Long scheduleId,
            @RequestBody PaymentScheduleStatus status) {
        try {
            PaymentSchedule schedule = paymentService.updateInstallmentStatus(scheduleId, status);
            return ResponseEntity.ok(schedule);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/subscription/{subscriptionId}")
    public ResponseEntity<List<Payment>> getSubscriptionPayments(
            @PathVariable Long subscriptionId) {
        try {
            List<Payment> payments = paymentService.getPaymentsBySubscription(subscriptionId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{paymentId}/schedules")
    public ResponseEntity<List<PaymentSchedule>> getPaymentSchedules(
            @PathVariable Long paymentId) {
        try {
            List<PaymentSchedule> schedules = paymentService.getPaymentSchedules(paymentId);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{paymentId}/invoice")
    public ResponseEntity<Invoice> getPaymentInvoice(@PathVariable Long paymentId) {
        try {
            Invoice invoice = paymentService.getInvoice(paymentId);
            return ResponseEntity.ok(invoice);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{paymentId}/overdue")
    public ResponseEntity<Boolean> isPaymentOverdue(@PathVariable Long paymentId) {
        try {
            boolean isOverdue = paymentService.isPaymentOverdue(paymentId);
            return ResponseEntity.ok(isOverdue);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
