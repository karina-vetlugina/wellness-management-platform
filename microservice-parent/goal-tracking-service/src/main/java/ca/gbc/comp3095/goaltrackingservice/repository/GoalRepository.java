package ca.gbc.comp3095.goaltrackingservice.repository;

import ca.gbc.comp3095.goaltrackingservice.model.Goal;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GoalRepository extends MongoRepository<Goal, String> {
    List<Goal> findByStatus(String status);
    /*List<Goal> findByCategory(String category);
    List<Goal> findByStatusAndCategory(String status, String category);*/
}