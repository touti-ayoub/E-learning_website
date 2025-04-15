package tn.esprit.microservice4.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import  tn.esprit.microservice4.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
