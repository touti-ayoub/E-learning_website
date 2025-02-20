package tn.esprit.microservice2.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microservice2.Model.Subscription;

public interface ISubscriptionRepository extends JpaRepository<Subscription, Long> {
}
