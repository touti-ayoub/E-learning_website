package tn.esprit.tpfoyer17.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.tpfoyer17.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
