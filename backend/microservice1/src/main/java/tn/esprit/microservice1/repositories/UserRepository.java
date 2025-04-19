package tn.esprit.microservice1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.microservice1.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // No additional methods needed
} 