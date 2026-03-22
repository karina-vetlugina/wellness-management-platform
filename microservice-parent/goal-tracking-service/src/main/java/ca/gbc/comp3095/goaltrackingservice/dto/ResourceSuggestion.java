package ca.gbc.comp3095.goaltrackingservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceSuggestion {

    private String id;
    private String title;
    private String categoryTitle;
    private String url;
}