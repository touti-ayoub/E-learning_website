package tn.esprit.microservice2.Rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice2.DTO.SubscriptionPlanDTO;
import tn.esprit.microservice2.service.SubscriptionPlanService;

import java.util.List;

@RestController
@RequestMapping("/mic2/subscription-plans")
public class SubscriptionPlanController {

    @Autowired
    private SubscriptionPlanService subscriptionPlanService;

    @PostMapping
    public ResponseEntity<SubscriptionPlanDTO> createSubscriptionPlan(@RequestBody SubscriptionPlanDTO planDTO) {
        try {
            SubscriptionPlanDTO newPlan = subscriptionPlanService.createSubscriptionPlan(planDTO);
            return ResponseEntity.ok(newPlan);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionPlanDTO>> getAllSubscriptionPlans() {
        List<SubscriptionPlanDTO> plans = subscriptionPlanService.getAllSubscriptionPlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionPlanDTO> getSubscriptionPlanById(@PathVariable Long id) {
        try {
            SubscriptionPlanDTO plan = subscriptionPlanService.getSubscriptionPlanById(id);
            return ResponseEntity.ok(plan);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionPlanDTO> updateSubscriptionPlan(@PathVariable Long id, @RequestBody SubscriptionPlanDTO planDTO) {
        try {
            SubscriptionPlanDTO updatedPlan = subscriptionPlanService.updateSubscriptionPlan(id, planDTO);
            return ResponseEntity.ok(updatedPlan);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscriptionPlan(@PathVariable Long id) {
        try {
            subscriptionPlanService.deleteSubscriptionPlan(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
