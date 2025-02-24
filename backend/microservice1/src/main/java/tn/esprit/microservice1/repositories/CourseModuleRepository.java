package tn.esprit.microservice1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microservice1.entities.CourseModule;

public interface CourseModuleRepository extends JpaRepository<CourseModule, Long> {
}
