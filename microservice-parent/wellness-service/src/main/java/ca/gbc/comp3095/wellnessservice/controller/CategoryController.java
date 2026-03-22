package ca.gbc.comp3095.wellnessservice.controller;

import ca.gbc.comp3095.wellnessservice.dto.CategoryRequest;
import ca.gbc.comp3095.wellnessservice.dto.CategoryResponse;
import ca.gbc.comp3095.wellnessservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/wellness-resource/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService _categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest categoryRequest) {
        CategoryResponse categoryResponse = _categoryService.createCategory(categoryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponse);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAllCategories() {
        return ResponseEntity.ok(_categoryService.findAll());
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable("categoryId") String categoryId, @RequestBody CategoryRequest categoryRequest) {
        String updatedCategoryId = _categoryService.updateCategory(categoryId, categoryRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/category/" + updatedCategoryId);
        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable("categoryId") String categoryId) {
        boolean isDeleted = _categoryService.deleteCategory(categoryId);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
