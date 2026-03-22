//package ca.gbc.comp3095.eventservice.events;
//
//import ca.gbc.comp3095.eventservice.dto.GoalCompletedEvent;
//import ca.gbc.comp3095.eventservice.service.EventRecommendationService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.support.Acknowledgment;
//import org.springframework.kafka.support.KafkaHeaders;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class GoalCompletedEventConsumer {
//
//    private final EventRecommendationService eventRecommendationService;
//
//    @KafkaListener(
//            topics = "${kafka.topics.goal-completed}",
//            groupId = "${spring.kafka.consumer.group-id}",
//            containerFactory = "kafkaListenerContainerFactory"
//    )
//    public void consumeGoalCompletedEvent(
//            @Payload GoalCompletedEvent event,
//            @Header(KafkaHeaders.RECEIVED_KEY) String key,
//            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
//            Acknowledgment acknowledgment
//    ) {
//        try {
//            log.info("Received GoalCompletedEvent: goalId={}, category={}, version={}",
//                    event.getGoalId(), event.getCategory(), event.getEventVersion());
//
//            //handle schema versioning: check event version
//            if (event.getEventVersion() == null || !"v1".equals(event.getEventVersion())) {
//                log.warn("Unknown or unsupported event version: {}. Skipping event for goalId={}",
//                        event.getEventVersion(), event.getGoalId());
//                acknowledgment.acknowledge();
//                return;
//            }
//
//            //recommend relevant wellness events based on goal category
//            if (event.getCategory() != null && !event.getCategory().isBlank()) {
//                eventRecommendationService.recommendEventsForGoalCategory(
//                        event.getCategory(),
//                        event.getGoalId(),
//                        event.getUserId()
//                );
//            } else {
//                log.info("Goal category is null or blank, skipping recommendation for goalId={}",
//                        event.getGoalId());
//            }
//
//            acknowledgment.acknowledge();
//            log.info("Successfully processed GoalCompletedEvent for goalId={}", event.getGoalId());
//
//        } catch (Exception e) {
//            log.error("Error processing GoalCompletedEvent for goalId={}", event.getGoalId(), e);
//            acknowledgment.acknowledge();
//        }
//    }
//}
//
