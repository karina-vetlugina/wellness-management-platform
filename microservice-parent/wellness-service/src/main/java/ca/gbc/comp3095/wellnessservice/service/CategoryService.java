package ca.gbc.comp3095.wellnessservice.service;

import ca.gbc.comp3095.wellnessservice.dto.CategoryRequest;
import ca.gbc.comp3095.wellnessservice.dto.CategoryResponse;
import ca.gbc.comp3095.wellnessservice.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest categoryRequest);
    List<CategoryResponse> findAll();
    String updateCategory(String categoryId, CategoryRequest categoryRequest);
    boolean deleteCategory(String categoryId);
    Category findCategoryById(String categoryId);
}
