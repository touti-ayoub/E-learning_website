package tn.esprit.microservice2.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.microservice2.Model.SubscriptionPlan;
import tn.esprit.microservice2.repo.ISubscriptionPlanRepository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionPlanService {

    @Autowired
    private ISubscriptionPlanRepository subscriptionPlanRepository;

    // Create
    public SubscriptionPlan createSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        subscriptionPlan.setCreatedAt(LocalDateTime.now());
        return subscriptionPlanRepository.save(subscriptionPlan);
    }

    // Read (Get by ID)
    public Optional<SubscriptionPlan> getSubscriptionPlanById(Long id) {
        return subscriptionPlanRepository.findById(id);
    }

    // Read (Get All)
    public List<SubscriptionPlan> getAllSubscriptionPlans() {
        return subscriptionPlanRepository.findAll();
    }

    // Update
    public SubscriptionPlan updateSubscriptionPlan(Long id, SubscriptionPlan updatedPlan) {
        return subscriptionPlanRepository.findById(id)
                .map(plan -> {
                    plan.setName(updatedPlan.getName());
                    plan.setDescription(updatedPlan.getDescription());
                    plan.setPrice(updatedPlan.getPrice());
                    plan.setBillingCycle(updatedPlan.getBillingCycle());
                    plan.setInstallmentOption(updatedPlan.isInstallmentOption());
                    plan.setMaxInstallments(updatedPlan.getMaxInstallments());
                    plan.setDiscountRate(updatedPlan.getDiscountRate());
                    plan.setCreatedAt(LocalDateTime.now());
                    return subscriptionPlanRepository.save(plan);
                })
                .orElseThrow(() -> new RuntimeException("SubscriptionPlan not found with id: " + id));
    }

    // Delete
    public void deleteSubscriptionPlan(Long id) {
        subscriptionPlanRepository.deleteById(id);
    }
}