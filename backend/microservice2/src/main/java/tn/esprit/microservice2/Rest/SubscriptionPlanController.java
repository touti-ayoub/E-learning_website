package tn.esprit.microservice2.Rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice2.Model.SubscriptionPlan;
import tn.esprit.microservice2.service.SubscriptionPlanService;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/mic2/subPlan")
public class SubscriptionPlanController {

    @Autowired
    private SubscriptionPlanService subscriptionPlanService;

    @PostMapping
    public SubscriptionPlan createSubscriptionPlan(@RequestBody SubscriptionPlan subscriptionPlan) {
        return subscriptionPlanService.createSubscriptionPlan(subscriptionPlan);
    }

    @GetMapping("/{id}")
    public Optional<SubscriptionPlan> getSubscriptionPlanById(@PathVariable Long id) {
        return subscriptionPlanService.getSubscriptionPlanById(id);
    }

    @GetMapping
    public List<SubscriptionPlan> getAllSubscriptionPlans() {
        return subscriptionPlanService.getAllSubscriptionPlans();
    }

    @PutMapping("/{id}")
    public SubscriptionPlan updateSubscriptionPlan(@PathVariable Long id, @RequestBody SubscriptionPlan updatedPlan) {
        return subscriptionPlanService.updateSubscriptionPlan(id, updatedPlan);
    }

    @DeleteMapping("/{id}")
    public void deleteSubscriptionPlan(@PathVariable Long id) {
        subscriptionPlanService.deleteSubscriptionPlan(id);
    }
}