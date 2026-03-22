//package ca.gbc.comp3095.goaltrackingservice.events;
//
//import ca.gbc.comp3095.goaltrackingservice.event.GoalCompletedEvent;
//import ca.gbc.comp3095.goaltrackingservice.model.Goal;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
//
//import java.time.OffsetDateTime;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class GoalEventProducer {
//
//    // Spring Boot auto-configures this KafkaTemplate using application*.properties
//    private final KafkaTemplate<String, GoalCompletedEvent> kafkaTemplate;
//
//    // Reads the topic name from application.properties / application-docker.properties
//    @Value("${kafka.topics.goal-completed}")
//    private String goalCompletedTopic;
//
//    public void publishGoalCompleted(Goal goal) {
//        // 1) Build the event payload ( JSON schema)
//        GoalCompletedEvent event = GoalCompletedEvent.builder()
//                .eventVersion("v1")               // schema version
//                .goalId(goal.getId())
//                .title(goal.getTitle())
//                .category(goal.getCategory())
//                .userId(null)
//                .completedAt(OffsetDateTime.now().toString())
//                .build();
//
//        // 2) Send asynchronously to Kafka
//        kafkaTemplate
//                .send(goalCompletedTopic, event.getGoalId(), event)
//                .whenComplete((result, ex) -> {
//                    if (ex != null) {
//                        log.error("Failed to publish GoalCompletedEvent for goalId={}", goal.getId(), ex);
//                    } else {
//                        log.info("Published GoalCompletedEvent for goalId={} to topic={}",
//                                goal.getId(), goalCompletedTopic);
//                    }
//                });
//    }
//}