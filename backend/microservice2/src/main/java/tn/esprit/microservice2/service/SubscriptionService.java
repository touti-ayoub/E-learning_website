package tn.esprit.microservice2.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.microservice2.Model.Subscription;
import tn.esprit.microservice2.Model.SubscriptionPlan;
import tn.esprit.microservice2.Model.User;
import tn.esprit.microservice2.repo.ISubscriptionPlanRepository;
import tn.esprit.microservice2.repo.ISubscriptionRepository;
import tn.esprit.microservice2.repo.UserRepository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    private ISubscriptionRepository subscriptionRepository;

    @Autowired
    private ISubscriptionPlanRepository subscriptionPlanRepository;

    @Autowired
    private UserRepository userRepository;

    // Create
    public Subscription createSubscription(Long userId, Long planId, Subscription subscription) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("SubscriptionPlan not found with id: " + planId));

        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.setCreatedAt(LocalDateTime.now());
        subscription.setUpdatedAt(LocalDateTime.now());
        return subscriptionRepository.save(subscription);
    }

    // Read (Get by ID)
    public Optional<Subscription> getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id);
    }

    // Read (Get All)
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    // Update
    public Subscription updateSubscription(Long id, Subscription updatedSubscription) {
        return subscriptionRepository.findById(id)
                .map(subscription -> {
                    subscription.setStartDate(updatedSubscription.getStartDate());
                    subscription.setEndDate(updatedSubscription.getEndDate());
                    subscription.setStatus(updatedSubscription.getStatus());
                    subscription.setAutoRenew(updatedSubscription.isAutoRenew());
                    subscription.setUpdatedAt(LocalDateTime.now());
                    return subscriptionRepository.save(subscription);
                })
                .orElseThrow(() -> new RuntimeException("Subscription not found with id: " + id));
    }

    // Delete
    public void deleteSubscription(Long id) {
        subscriptionRepository.deleteById(id);
    }
}