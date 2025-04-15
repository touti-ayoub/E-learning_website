package tn.esprit.microservice4.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.esprit.microservice4.entities.Point;

import java.util.List;

public interface PointRepository extends JpaRepository<Point, Long> {

    List<Point> findByUserProgressId(Long userProgressId);

    @Query("SELECT COALESCE(SUM(p.pointWins), 0) FROM Point p WHERE p.userProgress.id = :userProgressId")
    int getTotalPointsByUserProgress(Long userProgressId);
}
