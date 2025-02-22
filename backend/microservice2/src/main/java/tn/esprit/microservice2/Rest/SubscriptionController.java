package tn.esprit.microservice2.Rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice2.Model.PaymentType;
import tn.esprit.microservice2.Model.Subscription;
import tn.esprit.microservice2.Model.SubscriptionStatus;
import tn.esprit.microservice2.service.SubscriptionService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/mic2/subscription")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping("/test")
    public String test() {
        return "Subscription backend work !!!";
    }


    @PostMapping("/user/{userId}/plan/{planId}")
    public Subscription createSubscription(
            @PathVariable Long userId,
            @PathVariable Long planId,
            @RequestBody Subscription subscription) {
        return subscriptionService.createSubscription(userId, planId, subscription);
    }

    @GetMapping("/{id}")
    public Optional<Subscription> getSubscriptionById(@PathVariable Long id) {
        return subscriptionService.getSubscriptionById(id);
    }

    @GetMapping
    public List<Subscription> getAllSubscriptions() {
        return subscriptionService.getAllSubscriptions();
    }

    @PutMapping("/{id}")
    public Subscription updateSubscription(@PathVariable Long id, @RequestBody Subscription updatedSubscription) {
        return subscriptionService.updateSubscription(id, updatedSubscription);
    }

    @DeleteMapping("/{id}")
    public void deleteSubscription(@PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
    }

    // Créer un abonnement
    @PostMapping("/create")
    public Subscription createSubscription(@RequestParam Long userId, @RequestParam Long courseId, @RequestBody PaymentType paymentType) {
        return subscriptionService.createSubscription(userId, courseId, paymentType);
    }

    // Annuler un abonnement
    @PutMapping("/cancel/{subscriptionId}")
    public void cancelSubscription(@PathVariable Long subscriptionId) {
        subscriptionService.cancelSubscription(subscriptionId);
    }

    // Mettre à jour l'abonnement
    @PutMapping("/update/{subscriptionId}")
    public Subscription updateSubscription(@PathVariable Long subscriptionId, @RequestBody SubscriptionStatus status) {
        return subscriptionService.updateSubscription(subscriptionId, status);
    }
}