package tn.esprit.microservice2.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microservice2.Model.Facture;

public interface IFactureRepository extends JpaRepository<Facture, Long> {
}
