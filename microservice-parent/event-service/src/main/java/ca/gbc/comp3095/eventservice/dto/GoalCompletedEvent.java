package ca.gbc.comp3095.eventservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalCompletedEvent {

    private String eventVersion;

    private String goalId;
    private String title;
    private String category;
    private String userId;
    private String completedAt;
}
