package tn.esprit.microservice2.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microservice2.Model.SubscriptionPlan;

public interface ISubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
}