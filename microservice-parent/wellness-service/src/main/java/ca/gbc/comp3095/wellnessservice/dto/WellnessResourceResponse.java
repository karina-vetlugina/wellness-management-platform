package ca.gbc.comp3095.wellnessservice.dto;

import java.io.Serializable;

public record WellnessResourceResponse(
        String id,
        String title,
        String description,
        String url,
        String categoryId,
        String categoryTitle
) implements Serializable {
}
