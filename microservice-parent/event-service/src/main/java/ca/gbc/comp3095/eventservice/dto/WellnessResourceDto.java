package ca.gbc.comp3095.eventservice.dto;

public record WellnessResourceDto(
        String id,
        String title,
        String description,
        String url,
        String categoryId,
        String categoryTitle
) { }