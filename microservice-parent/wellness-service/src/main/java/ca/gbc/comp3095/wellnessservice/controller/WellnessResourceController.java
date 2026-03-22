package ca.gbc.comp3095.wellnessservice.controller;

import ca.gbc.comp3095.wellnessservice.dto.WellnessResourceRequest;
import ca.gbc.comp3095.wellnessservice.dto.WellnessResourceResponse;
import ca.gbc.comp3095.wellnessservice.service.WellnessResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("api/wellness-resource")
@RequiredArgsConstructor
public class WellnessResourceController {
    private final WellnessResourceService _wellnessResourceService;

    @PostMapping
    public ResponseEntity<?> createWellnessResource(@RequestBody WellnessResourceRequest wellnessResourceRequest) {
        WellnessResourceResponse wellnessResourceResponse = _wellnessResourceService.createResource(wellnessResourceRequest);
        if (wellnessResourceResponse != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(wellnessResourceResponse);
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Invalid category ID: " + wellnessResourceRequest.categoryId());
    }

    @GetMapping
    public ResponseEntity<List<WellnessResourceResponse>> findAllWellnessResource(
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String keyword
    ) {

        boolean hasCategory = categoryId != null && !categoryId.isBlank();
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        if (hasCategory && hasKeyword) {
            return ResponseEntity.ok(_wellnessResourceService.findByCategoryIdAndKeyword(categoryId, keyword));
        }
        if (hasCategory) {
            return ResponseEntity.ok(_wellnessResourceService.findByCategoryId(categoryId));
        }
        if (hasKeyword) {
            return ResponseEntity.ok(_wellnessResourceService.findByKeyword(keyword));
        }

        return ResponseEntity.ok(_wellnessResourceService.findAll());
    }

    @GetMapping("/{resourceId}")
    public ResponseEntity<?> findResourceById(@PathVariable String resourceId) {
        WellnessResourceResponse wellnessResourceResponse = _wellnessResourceService.getResourceById(resourceId);
        if (wellnessResourceResponse != null) {
            return ResponseEntity.ok(wellnessResourceResponse);
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Invalid resource ID: " + resourceId);
    }

    @PutMapping("/{resourceId}")
    public ResponseEntity<?> updateWellnessResource(@PathVariable("resourceId") String resourceId, @RequestBody WellnessResourceRequest wellnessResourceRequest) {
        String updatedWellnessResourceId = _wellnessResourceService.updateResource(resourceId, wellnessResourceRequest);
        if (updatedWellnessResourceId != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "/api/wellness-resource/" + updatedWellnessResourceId);
            return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Invalid category ID: " + resourceId);
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<?> deleteWellnessResource(@PathVariable("resourceId") String resourceId) {
        boolean isDeleted = _wellnessResourceService.deleteResource(resourceId);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
