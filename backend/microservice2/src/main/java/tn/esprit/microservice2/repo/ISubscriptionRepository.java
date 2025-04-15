package tn.esprit.microservice2.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.microservice2.Model.Course;
import tn.esprit.microservice2.Model.Subscription;
import tn.esprit.microservice2.Model.SubscriptionStatus;
import tn.esprit.microservice2.Model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ISubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUser(User user);
    Optional<Subscription> findByUserAndCourseAndStatus(User user, Course course, SubscriptionStatus status);
    Optional<Subscription> findByUserIdAndCourseIdAndStatus(Long userId, Long courseId, SubscriptionStatus status);
    @Query("SELECT s FROM Subscription s " +
            "LEFT JOIN FETCH s.user " +
            "LEFT JOIN FETCH s.course " +
            "WHERE s.id = :id")
    Optional<Subscription> findByIdWithDetails(@Param("id") Long id);

    // Add these methods to your SubscriptionRepository
    long countByStatusAndEndDateAfter(SubscriptionStatus status, LocalDateTime date);
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    long countByStatusAndCreatedAtBetween(SubscriptionStatus status, LocalDateTime start, LocalDateTime end);
    long countByStatusAndUpdatedAtBetweenAndStartDateLessThan(SubscriptionStatus status, LocalDateTime start, LocalDateTime end, LocalDateTime date);
    long countByStatusAndEndDateBetween(SubscriptionStatus status, LocalDateTime start, LocalDateTime end);
    long countByStatusAndUpdatedAtBetween(SubscriptionStatus status, LocalDateTime start, LocalDateTime end);
}