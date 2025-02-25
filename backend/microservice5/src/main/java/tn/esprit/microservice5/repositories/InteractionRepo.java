package tn.esprit.microservice5.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microservice5.entities.Interaction;

public interface InteractionRepo extends JpaRepository<Interaction, Integer> {
}
