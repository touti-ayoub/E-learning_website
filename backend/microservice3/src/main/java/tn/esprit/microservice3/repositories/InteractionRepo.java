package tn.esprit.microservice3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microservice3.entities.Interaction;

public interface InteractionRepo extends JpaRepository<Interaction, Integer> {
}
