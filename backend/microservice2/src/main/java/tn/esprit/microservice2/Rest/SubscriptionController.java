package tn.esprit.microservice2.Rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice2.DTO.SubCreatingRequest;
import tn.esprit.microservice2.DTO.SubscriptionDTO;
import tn.esprit.microservice2.Model.*;
import tn.esprit.microservice2.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/mic2/subscription")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping("/test")
    public String test() {
        return "Subscription backend work !!!";
    }


    /*@PostMapping("/user/{userId}/plan/{planId}")
    public Subscription createSubscription(
            @PathVariable Long userId,
            @PathVariable Long planId,
            @RequestBody Subscription subscription) {
        return subscriptionService.createSubscription(userId, planId, subscription);
    }*/

    /*@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SubscriptionDTO> getSubscriptionById(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionById(id));
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SubscriptionDTO>> getAllSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }*/
    /*
    @PutMapping("/{id}")
    public Subscription updateSubscription(@PathVariable Long id, @RequestBody Subscription updatedSubscription) {
        return subscriptionService.updateSubscription(id, updatedSubscription);
    }

    @DeleteMapping("/{id}")
    public void deleteSubscription(@PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
    }*/

    // ✅ Créer un abonnement
   /* @PostMapping("/create")
    public SubscriptionDTO createSubscription(@RequestBody SubCreatingRequest subRequest) {
        return subscriptionService.createSubscription(subRequest);
    }

    // Annuler un abonnement
    @PutMapping("/cancel/{subscriptionId}")
    public void cancelSubscription(@PathVariable Long subscriptionId) {
        subscriptionService.cancelSubscription(subscriptionId);
    }



    // ✅ Mettre à jour un abonnement
    @PutMapping("/update/{subscriptionId}")
    public SubscriptionDTO updateSubscription(@PathVariable Long subscriptionId, @RequestBody SubscriptionStatus status) {
        return subscriptionService.updateSubscription(subscriptionId, status);
    }
}*/



    @PostMapping
    public ResponseEntity<SubscriptionDTO> createSubscription(@RequestBody SubCreatingRequest request) {
        try {
            SubscriptionDTO subscription = subscriptionService.createSubscription(request);
            return ResponseEntity.ok(subscription);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionDTO> getSubscription(@PathVariable Long id) {
        try {
            SubscriptionDTO subscription = subscriptionService.getSubscriptionById(id);
            return ResponseEntity.ok(subscription);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionDTO>> getAllSubscriptions() {
        List<SubscriptionDTO> subscriptions = subscriptionService.getAllSubscriptions();
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SubscriptionDTO>> getUserSubscriptions(@PathVariable Long userId) {
        try {
            List<SubscriptionDTO> subscriptions = subscriptionService.getUserSubscriptions(userId);
            return ResponseEntity.ok(subscriptions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<SubscriptionDTO> updateSubscriptionStatus(
            @PathVariable Long id,
            @RequestBody SubscriptionStatus status) {
        try {
            SubscriptionDTO subscription = subscriptionService.updateSubscriptionStatus(id, status);
            return ResponseEntity.ok(subscription);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelSubscription(@PathVariable Long id) {
        try {
            subscriptionService.cancelSubscription(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        try {
            subscriptionService.deleteSubscription(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/getUserByUN/{un}")
    public ResponseEntity<User> getSubscription(@PathVariable String un) {
        try {
            User user = subscriptionService.getUserByUsername(un);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}