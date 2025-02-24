package tn.esprit.microservice1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.microservice1.entities.CourseProgress;

@Repository
public interface CourseProgressRepository extends JpaRepository<CourseProgress, Long> {
}
