package ca.gbc.comp3095.goaltrackingservice.service;

import ca.gbc.comp3095.goaltrackingservice.client.WellnessClient;
import ca.gbc.comp3095.goaltrackingservice.dto.GoalRequest;
import ca.gbc.comp3095.goaltrackingservice.dto.ResourceSuggestion;
import ca.gbc.comp3095.goaltrackingservice.model.Goal;
import ca.gbc.comp3095.goaltrackingservice.repository.GoalRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ca.gbc.comp3095.goaltrackingservice.event.GoalCompletedEvent;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalServiceImpl implements GoalService {

    private final GoalRepository _goalRepository;
    private final WellnessClient _wellnessClient;
//    private final GoalEventProducer _goalEventProducer;
    private final KafkaTemplate<String, GoalCompletedEvent> _kafkaTemplate;


    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE; // yyyy-MM-dd

    @Override
    public Goal createGoal(GoalRequest request) {
        Goal goal = Goal.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .targetDate(parseDate(request.getTargetDate()))
                .status(request.getStatus())
                .category(request.getCategory())
                .build();
        return _goalRepository.save(goal);
    }

    @Override
    public List<Goal> getAllGoals() {
        return _goalRepository.findAll();
    }

    @Override
    public Goal updateGoal(String id, GoalRequest request) {
        Goal existing = _goalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found: " + id));

        existing.setTitle(request.getTitle());
        existing.setDescription(request.getDescription());
        existing.setTargetDate(parseDate(request.getTargetDate()));
        existing.setStatus(request.getStatus());
        existing.setCategory(request.getCategory());

        return _goalRepository.save(existing);
    }

    @Override
    public void deleteGoal(String id) {
        if (!_goalRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found: " + id);
        }
        _goalRepository.deleteById(id);
    }

    @Override
    public Goal markCompleted(String id) {
        Goal existing = _goalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found: " + id));

        existing.setStatus("COMPLETED");
        Goal saved = _goalRepository.save(existing);

        GoalCompletedEvent goalCompletedEvent = new  GoalCompletedEvent();
        goalCompletedEvent.setTitle(saved.getTitle());
        goalCompletedEvent.setDescription(saved.getDescription());
        goalCompletedEvent.setCategory(saved.getCategory());

        log.info("Start - sending goalCompletedEvent {} to Kafka topic 'goal-completed'", goalCompletedEvent);
        _kafkaTemplate.send("goal-completed", goalCompletedEvent);
        log.info("End - goalCompletedEvent {} sent to Kafka topic 'goal-completed'", goalCompletedEvent);


        return saved;
    }

    @Override
    public List<ResourceSuggestion> suggestResources(String goalId) {
        Goal goal = _goalRepository.findById(goalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found: " + goalId));

        String category = goal.getCategory();
        if (category == null || category.isBlank()) return List.of();

        try {
            return _wellnessClient.findByKeyword(category);
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Wellness service error", e);
        }
    }


    private LocalDate parseDate(String raw) {
        if (raw == null) return null;
        try {
            return LocalDate.parse(raw, ISO);
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "targetDate must be in yyyy-MM-dd format (e.g., 2025-11-15)"
            );
        }
    }

    @Override
    public List<Goal> getGoalsByStatus(String status) {
        if (status == null || status.isBlank()) {
            return _goalRepository.findAll(); // fallback: all goals
        }

        String normalized = status.trim().toUpperCase(); // handle any casing
        return _goalRepository.findByStatus(normalized);
    }
}