package ca.gbc.comp3095.wellnessservice.service;

import ca.gbc.comp3095.wellnessservice.dto.CategoryRequest;
import ca.gbc.comp3095.wellnessservice.dto.CategoryResponse;
import ca.gbc.comp3095.wellnessservice.model.Category;
import ca.gbc.comp3095.wellnessservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository _categoryRepository;


    @Override
    @Caching(
            put = {
                    @CachePut(value = "categoryById", key = "#result.id()")
            },
            evict = {
                    @CacheEvict(value = "categoryAll", allEntries = true)
            }
    )
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        Category category = Category.builder()
                .id(UUID.randomUUID().toString())
                .title(categoryRequest.title())
                .description(categoryRequest.description())
                .build();
        _categoryRepository.save(category);


        return new CategoryResponse(category.getId(), category.getTitle(), category.getDescription());
    }

    @Override
    @Cacheable(value = "categoryAll", unless = "#result.isEmpty()")
    public List<CategoryResponse> findAll() {
        List<Category> categories = _categoryRepository.findAll();
        return categories.stream()
                .map(this::mapToCategoryResponse)
                .toList();
    }

    @Override
    @Caching(
            put = {
                    @CachePut(value = "categoryById", key = "#categoryId")
            },
            evict = {
                    @CacheEvict(value = "categoryAll", allEntries = true)
            }
    )
    public String updateCategory(String categoryId, CategoryRequest categoryRequest) {
        Optional<Category> optionalCategory = _categoryRepository.findById(categoryId);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            category.setTitle(categoryRequest.title());
            category.setDescription(categoryRequest.description());
            return _categoryRepository.save(category).getId();
        }
        return categoryId;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "categoryById", key = "#categoryId"),
            @CacheEvict(value = "categoryAll", allEntries = true)
    })
    public boolean deleteCategory(String categoryId) {
        Optional<Category> optionalCategory = _categoryRepository.findById(categoryId);
        if (optionalCategory.isPresent()) {
            _categoryRepository.deleteById(categoryId);
            return true;
        }
        return false;

    }

    @Override
    @CachePut(value = "categoryById", key = "#categoryId")
    public Category findCategoryById(String categoryId) {
        Optional<Category> optionalCategory = _categoryRepository.findById(categoryId);
        if (optionalCategory.isPresent()) {
            return optionalCategory.get();
        }
        return null;
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getTitle(), category.getDescription());
    }
}
