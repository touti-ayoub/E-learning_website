package tn.esprit.microservice1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.microservice1.entities.Lesson;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
}
