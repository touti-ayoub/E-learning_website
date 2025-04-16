package tn.esprit.microservice5.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.microservice5.Model.Registration;
import tn.esprit.microservice5.Model.RegistrationStatus;

import java.util.List;

@Repository
public interface IRegistrationRepo extends JpaRepository<Registration, Long> {

    /**
     * Find all registrations for a specific user
     */
    List<Registration> findByUserUserId(long userId);

    /**
     * Find all registrations for a specific event
     */
    List<Registration> findByEventEventId(long eventId);

    /**
     * Check if a user is already registered for an event
     */
    boolean existsByUserUserIdAndEventEventId(long userId, long eventId);

    /**
     * Count registrations for an event with a specific status
     */
    long countByEventEventIdAndStatus(long eventId, RegistrationStatus status);
}