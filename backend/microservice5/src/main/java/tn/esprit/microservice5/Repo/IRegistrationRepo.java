package tn.esprit.microservice5.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microservice5.Model.Registration;

public interface IRegistrationRepo extends JpaRepository<Registration, Long> {
}