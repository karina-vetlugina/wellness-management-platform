package ca.gbc.comp3095.eventservice.repository;

import ca.gbc.comp3095.eventservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // find all events that contain a specific word in the location
    List<Event> findAllByLocationIgnoreCaseContaining(String location);
    // find all events between two dates
    List<Event> findAllByEventDateBetween(LocalDateTime from, LocalDateTime to);
    
}
