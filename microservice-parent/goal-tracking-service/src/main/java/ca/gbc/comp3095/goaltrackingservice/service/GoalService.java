package ca.gbc.comp3095.goaltrackingservice.service;

import ca.gbc.comp3095.goaltrackingservice.dto.GoalRequest;
import ca.gbc.comp3095.goaltrackingservice.dto.ResourceSuggestion;
import ca.gbc.comp3095.goaltrackingservice.model.Goal;

import java.util.List;

public interface GoalService {

    // CREATE
    Goal createGoal(GoalRequest request);

    // READ
    List<Goal> getAllGoals();

    // UPDATE
    Goal updateGoal(String id, GoalRequest request);

    // DELETE
    void deleteGoal(String id);

    // MARK COMPLETE
    Goal markCompleted(String id);

    List<ResourceSuggestion> suggestResources(String goalId);

    List<Goal> getGoalsByStatus(String status);
}

