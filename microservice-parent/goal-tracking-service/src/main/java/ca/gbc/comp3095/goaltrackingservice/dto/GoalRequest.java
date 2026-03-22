package ca.gbc.comp3095.goaltrackingservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalRequest {
    //Add ID
    private String id;

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    @Size(max = 2000)
    private String description;

    @NotBlank
    private String targetDate;

    @NotBlank
    private String status;     // e.g., IN_PROGRESS, COMPLETED

    @NotBlank
    private String category;
}