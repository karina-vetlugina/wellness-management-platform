package ca.gbc.comp3095.goaltrackingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalResponse {
    private String id;
    private String title;
    private String description;
    private String targetDate;
    private String status;
    private String category;

}