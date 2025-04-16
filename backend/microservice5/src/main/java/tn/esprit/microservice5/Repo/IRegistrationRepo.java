package tn.esprit.microservice5.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.microservice5.Model.Registration;

import java.util.List;

@Repository
public interface IRegistrationRepo extends JpaRepository<Registration, Long> {
    List<Registration> findByUser_UserId(Long userId);
    List<Registration> findByEvent_EventId(Long eventId);
}