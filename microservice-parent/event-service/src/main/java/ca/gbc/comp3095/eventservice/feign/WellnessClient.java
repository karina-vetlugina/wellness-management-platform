package ca.gbc.comp3095.eventservice.feign;

import ca.gbc.comp3095.eventservice.dto.WellnessResourceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "wellnessClient",
        url = "${wellness.base-url}",
        path = "/api/wellness-resource"
)
public interface WellnessClient {

    @GetMapping
    List<WellnessResourceDto> findAll(
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String keyword
    );
}