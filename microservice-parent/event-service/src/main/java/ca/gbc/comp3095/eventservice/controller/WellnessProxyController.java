package ca.gbc.comp3095.eventservice.controller;

import ca.gbc.comp3095.eventservice.dto.WellnessResourceDto;
import ca.gbc.comp3095.eventservice.service.WellnessGatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/event/wellness")
public class WellnessProxyController {

    private final WellnessGatewayService wellnessGatewayService;

    @GetMapping
    public ResponseEntity<List<WellnessResourceDto>> proxyList(
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(wellnessGatewayService.listResources(categoryId, keyword));
    }
}