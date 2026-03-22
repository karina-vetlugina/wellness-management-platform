package ca.gbc.comp3095.wellnessservice.dto;

import java.io.Serializable;

public record CategoryResponse (
        String id,
        String title,
        String description
) implements Serializable{
}
