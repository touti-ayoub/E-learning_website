package tn.esprit.microservice2.Rest;


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
    @PostMapping("/create/{subscriptionId}")
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
    }
}
