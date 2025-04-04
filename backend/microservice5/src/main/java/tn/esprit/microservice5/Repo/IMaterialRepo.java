package tn.esprit.microservice5.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.microservice5.Model.Material;
import tn.esprit.microservice5.Model.Registration;
@Repository

public interface IMaterialRepo extends JpaRepository<Material, Long> {
}
