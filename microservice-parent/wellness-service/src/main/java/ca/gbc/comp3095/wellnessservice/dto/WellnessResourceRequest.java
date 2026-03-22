package ca.gbc.comp3095.wellnessservice.dto;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record WellnessResourceRequest(
        @NotBlank(message = "ID is required")
        String id,

        @NotBlank(message = "Title is required")
        String title,


        String description,

        @NotBlank(message = "URL is required")
        String url,

        @NotBlank(message = "Category ID is required")
        String categoryId

)implements Serializable {

}

