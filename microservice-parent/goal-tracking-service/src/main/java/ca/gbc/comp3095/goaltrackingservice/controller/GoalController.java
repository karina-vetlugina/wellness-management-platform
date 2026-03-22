package ca.gbc.comp3095.goaltrackingservice.controller;

import ca.gbc.comp3095.goaltrackingservice.dto.GoalRequest;
import ca.gbc.comp3095.goaltrackingservice.dto.GoalResponse;
import ca.gbc.comp3095.goaltrackingservice.dto.ResourceSuggestion;
import ca.gbc.comp3095.goaltrackingservice.model.Goal;
import ca.gbc.comp3095.goaltrackingservice.service.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/goal")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    // CREATE
    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(@Valid @RequestBody GoalRequest request) {
        // Ignore any id on create
        request.setId(null);
        Goal created = goalService.createGoal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    // READ (all)
    @GetMapping
    public ResponseEntity<List<GoalResponse>> getAllGoals() {
        List<GoalResponse> out = goalService.getAllGoals().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(out);
    }

    // GET by status (COMPLETED or IN_PROGRESS)
    @GetMapping(params = "status")
    public ResponseEntity<List<GoalResponse>> getGoalsByStatus(@RequestParam String status) {
        List<GoalResponse> out = goalService.getGoalsByStatus(status.trim().toUpperCase()).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(out);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateGoal(@PathVariable String id, @Valid @RequestBody GoalRequest request) {
        if (request.getId() != null && !id.equals(request.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body id does not match path id");
        }
        request.setId(id);

        Goal updated = goalService.updateGoal(id, request);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, "/api/goal/" + updated.getId());
        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable String id) {
        goalService.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }

    // MARK COMPLETE
    @PatchMapping("/{id}/complete")
    public ResponseEntity<GoalResponse> markGoalComplete(@PathVariable String id,
                                                         @RequestBody(required = false) GoalRequest request) {
        if (request != null && request.getId() != null && !id.equals(request.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body id does not match path id");
        }
        Goal updated = goalService.markCompleted(id);
        return ResponseEntity.ok(toResponse(updated));
    }

    // SUGGESTIONS (goal-tracking-service -> wellness-service)
    @GetMapping("/{id}/suggestions")
    public ResponseEntity<List<ResourceSuggestion>> suggestForGoal(@PathVariable String id) {
        return ResponseEntity.ok(goalService.suggestResources(id));
    }

    private GoalResponse toResponse(Goal g) {
        return GoalResponse.builder()
                .id(g.getId())
                .title(g.getTitle())
                .description(g.getDescription())
                .targetDate(g.getTargetDate() != null ? g.getTargetDate().format(ISO) : null)
                .status(g.getStatus())
                .category(g.getCategory())
                .build();
    }
}