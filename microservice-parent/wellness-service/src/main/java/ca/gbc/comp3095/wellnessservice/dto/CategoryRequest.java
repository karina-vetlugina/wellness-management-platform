package ca.gbc.comp3095.wellnessservice.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record CategoryRequest(
        @NotBlank(message = "ID is required")
        String id,
        @NotBlank(message = "Title is required")
        String title,
        String description
) implements Serializable {
}
