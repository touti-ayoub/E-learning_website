package tn.esprit.microservice5.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.microservice5.Model.Event;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IEventRepo extends JpaRepository<Event, Long> {
    List<Event> findByStartTimeAfter(LocalDateTime dateTime);

}
