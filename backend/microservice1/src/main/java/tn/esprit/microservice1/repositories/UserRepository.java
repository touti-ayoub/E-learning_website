package tn.esprit.microservice1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microservice1.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
