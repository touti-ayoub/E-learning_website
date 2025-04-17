package tn.esprit.microservice5.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microservice5.Model.User;

public interface IUserRepo extends JpaRepository<User, Long> {
}
