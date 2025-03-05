// Source code is decompiled from a .class file using FernFlower decompiler.
package tn.esprit.microservice1.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import tn.esprit.microservice1.entities.Course;
import tn.esprit.microservice1.entities.User;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByInstructor(User instructor);
    List<Course> findAllByOrderByCreatedAtDesc();

    @Query("SELECT c FROM Course c WHERE c.preferences LIKE %:userId% ORDER BY c.rating DESC")
    List<Course> findTopNByPreferences(Long userId, int limit);
}
