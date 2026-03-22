package ca.gbc.comp3095.goaltrackingservice.client;

import ca.gbc.comp3095.goaltrackingservice.dto.ResourceSuggestion;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "wellness-resource-service", url = "${wellness.base-url}")
public interface WellnessClient {

    @GetMapping("/api/wellness-resource")
    List<ResourceSuggestion> findByKeyword(@RequestParam("keyword") String keyword);
}