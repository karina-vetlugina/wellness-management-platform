package ca.gbc.comp3095.eventservice.service;

import ca.gbc.comp3095.eventservice.dto.WellnessResourceDto;
import ca.gbc.comp3095.eventservice.feign.WellnessClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WellnessGatewayService {
    private final WellnessClient wellnessClient;

    public List<WellnessResourceDto> listResources(String categoryId, String keyword) {
        try {
            return wellnessClient.findAll(categoryId, keyword);
        } catch (Exception e) {
            return List.of();
        }
    }
}