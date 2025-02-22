package tn.esprit.microservice2.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microservice2.Model.Course;

public interface ICourseRepository extends JpaRepository<Course, Long> {
}
