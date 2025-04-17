package tn.esprit.microservice5.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microservice5.Model.Feedback;
import tn.esprit.microservice5.Model.Registration;

public interface IFeedbackRepository extends JpaRepository<Feedback, Long> {
}
