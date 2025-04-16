package tn.esprit.microservice2.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.microservice2.DTO.SubscriptionPlanDTO;
import tn.esprit.microservice2.Model.SubscriptionPlan;
import tn.esprit.microservice2.repo.ISubscriptionPlanRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SubscriptionPlanService {
    private static final Logger log = LoggerFactory.getLogger(SubscriptionPlanService.class);

    @Autowired
    private ISubscriptionPlanRepository subscriptionPlanRepository;

    @Transactional
    public SubscriptionPlanDTO createSubscriptionPlan(SubscriptionPlanDTO planDTO) {
        log.debug("Creating subscription plan: {}", planDTO.getName());
        SubscriptionPlan plan = SubscriptionPlan.fromDTO(planDTO);
        plan = subscriptionPlanRepository.save(plan);
        return SubscriptionPlanDTO.fromEntity(plan);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionPlanDTO> getAllSubscriptionPlans() {
        log.debug("Fetching all subscription plans");
        return subscriptionPlanRepository.findAll().stream()
                .map(SubscriptionPlanDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SubscriptionPlanDTO getSubscriptionPlanById(Long id) {
        log.debug("Fetching subscription plan with id: {}", id);
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription plan not found with id: " + id));
        return SubscriptionPlanDTO.fromEntity(plan);
    }

    @Transactional
    public SubscriptionPlanDTO updateSubscriptionPlan(Long id, SubscriptionPlanDTO planDTO) {
        log.debug("Updating subscription plan: {}", id);
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription plan not found with id: " + id));

        plan.setName(planDTO.getName());
        plan.setDescription(planDTO.getDescription());
        plan.setPrice(planDTO.getPrice());
        plan.setDurationMonths(planDTO.getDurationMonths());
        plan = subscriptionPlanRepository.save(plan);
        return SubscriptionPlanDTO.fromEntity(plan);
    }

    @Transactional
    public void deleteSubscriptionPlan(Long id) {
        log.debug("Deleting subscription plan: {}", id);
        subscriptionPlanRepository.deleteById(id);
    }
}