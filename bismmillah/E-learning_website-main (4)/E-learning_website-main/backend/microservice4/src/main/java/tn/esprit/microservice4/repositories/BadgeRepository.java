package tn.esprit.microservice4.repositories;

import tn.esprit.microservice4.entities.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
}
