package ca.gbc.comp3095.eventservice.service;

import ca.gbc.comp3095.eventservice.model.Event;
import ca.gbc.comp3095.eventservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ca.gbc.comp3095.goaltrackingservice.event.GoalCompletedEvent;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventRecommendationServiceImpl {

    private final EventRepository eventRepository;

    @KafkaListener(topics = "goal-completed", groupId = "EventRecommendationService")
    public void recommendEventsForGoalCategory(GoalCompletedEvent event) {
        log.info("Finding recommended events for category: {}", event.getCategory());

        //find upcoming events (events in the future, up to 3 months ahead)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeMonthsAhead = now.plusMonths(3);

        //get all upcoming events
        List<Event> upcomingEvents = eventRepository.findAllByEventDateBetween(now, threeMonthsAhead);

        if (upcomingEvents.isEmpty()) {
            log.info("No upcoming events found for recommendation");
            return;
        }

        //filter events by category keyword matching in title or description
        String categoryLower = event.getCategory().toLowerCase();
        List<Event> recommendedEvents = upcomingEvents.stream()
                .filter(e -> {
                    boolean titleMatch = e.getTitle() != null &&
                            e.getTitle().toLowerCase().contains(categoryLower);
                    boolean descriptionMatch = e.getDescription() != null &&
                            e.getDescription().toLowerCase().contains(categoryLower);
                    return titleMatch || descriptionMatch;
                })
                .limit(5) //limit to top 5 recommendations
                .toList();

        if (recommendedEvents.isEmpty()) {
            log.info("No events found matching category: {}", event.getCategory());
        } else {
            log.info("Found {} recommended event(s) for category: {}",
                    recommendedEvents.size(), event.getCategory());

            //log recommended events for monitoring
            recommendedEvents.forEach(e ->
                    log.info("Recommended Event: '{}' (Date: {}, Location: {})",
                            e.getTitle(), e.getEventDate(), e.getLocation())
            );
        }

    }

}