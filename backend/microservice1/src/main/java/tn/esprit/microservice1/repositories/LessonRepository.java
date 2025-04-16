// LessonRepository.java
package tn.esprit.microservice1.repositories;

import tn.esprit.microservice1.entities.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
}