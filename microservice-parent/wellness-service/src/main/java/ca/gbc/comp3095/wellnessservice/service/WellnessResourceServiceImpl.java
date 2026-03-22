package ca.gbc.comp3095.wellnessservice.service;

import ca.gbc.comp3095.wellnessservice.dto.WellnessResourceRequest;
import ca.gbc.comp3095.wellnessservice.dto.WellnessResourceResponse;
import ca.gbc.comp3095.wellnessservice.model.Category;
import ca.gbc.comp3095.wellnessservice.model.WellnessResource;
import ca.gbc.comp3095.wellnessservice.repository.CategoryRepository;
import ca.gbc.comp3095.wellnessservice.repository.WellnessResourceRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WellnessResourceServiceImpl implements WellnessResourceService {

    private final WellnessResourceRepository _wellnessResourceRepository;
    private final CategoryRepository _categoryRepository;

    @Override
    @Caching(
            put = {
                    @CachePut(value = "wellnessResourceById", key = "#result.id()")
            },
            evict = {
                    @CacheEvict(value = "wellnessResourcesAll", allEntries = true)
            }
    )
    public WellnessResourceResponse createResource(WellnessResourceRequest wellnessResourceRequest) {
        String categoryId = wellnessResourceRequest.categoryId();
        Category category = _categoryRepository.findById(categoryId).orElse(null);
        if (category != null) {

            WellnessResource wellnessResource = WellnessResource.builder()
                    .id(UUID.randomUUID().toString())
                    .title(wellnessResourceRequest.title())
                    .description(wellnessResourceRequest.description())
                    .url(wellnessResourceRequest.url())
                    .category(category)
                    .build();
            _wellnessResourceRepository.save(wellnessResource);

            return new WellnessResourceResponse(wellnessResource.getId(),
                    wellnessResource.getTitle(),
                    wellnessResource.getDescription(),
                    wellnessResource.getUrl(),
                    wellnessResource.getCategory().getId(),
                    wellnessResource.getCategory().getTitle());
        }

        return null;
    }

    @Override
    @Cacheable(value = "wellnessResourcesAll", unless = "#result.isEmpty()")
    public List<WellnessResourceResponse> findAll() {
        List<WellnessResource> wellnessResourceList = _wellnessResourceRepository.findAll();
        return wellnessResourceList.stream()
                .map(this::mapToWellnessResourceResponse)
                .collect(Collectors
                        .toList());
    }

    @Override
    @Caching(
            put = {
                    @CachePut(value = "wellnessResourceById", key = "#resourceId")
            },
            evict = {
                    @CacheEvict(value = "wellnessResourcesAll", allEntries = true)
            }
    )
    public String updateResource(String resourceId, WellnessResourceRequest wellnessResourceRequest) {

        String categoryId = wellnessResourceRequest.categoryId();
        Optional<Category> optionalCategory = _categoryRepository.findById(categoryId);
        if (optionalCategory.isPresent()) {
            Optional<WellnessResource> optionalWellnessResource = _wellnessResourceRepository.findById(resourceId);
            if (optionalWellnessResource.isPresent()) {
                WellnessResource wellnessResource = optionalWellnessResource.get();
                wellnessResource.setTitle(wellnessResourceRequest.title());
                wellnessResource.setDescription(wellnessResourceRequest.description());
                wellnessResource.setUrl(wellnessResourceRequest.url());
                wellnessResource.setCategory(optionalCategory.get());
                return _wellnessResourceRepository.save(wellnessResource).getId();
            }
        }
        return categoryId;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "wellnessResourceById", key = "#resourceId"),
            @CacheEvict(value = "wellnessResourcesAll", allEntries = true)
    })
    public boolean deleteResource(String resourceId) {
        Optional<WellnessResource> wellnessResource = _wellnessResourceRepository.findById(resourceId);
        if (wellnessResource.isPresent()) {
            _wellnessResourceRepository.deleteById(resourceId);
            return true;
        }
        return false;
    }

    @Override
    @CachePut(value = "wellnessResourceById", key = "#resourceId")
    public WellnessResourceResponse getResourceById(String resourceId) {
        Optional<WellnessResource> optionalWellnessResource = _wellnessResourceRepository.findById(resourceId);
        if (optionalWellnessResource.isPresent()) {
            return mapToWellnessResourceResponse(optionalWellnessResource.get());
        }
        return null;
    }


    @Override
    @Cacheable(value = "wellnessResourcesAll")
    public List<WellnessResourceResponse> findByCategoryId(String categoryId) {
        categoryId = categoryId.trim().toLowerCase();
        List<WellnessResource> wellnessResourceList = _wellnessResourceRepository.findByCategoryId(categoryId);
        return wellnessResourceList.stream()
                .map(this::mapToWellnessResourceResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "wellnessResourcesAll")
    public List<WellnessResourceResponse> findByKeyword(String keyword) {
        keyword = keyword.trim().toLowerCase();
        List<WellnessResource> wellnessResourceList = _wellnessResourceRepository.findByKeyword(keyword);
        return wellnessResourceList.stream()
                .map(this::mapToWellnessResourceResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "wellnessResourcesAll")
    public List<WellnessResourceResponse> findByCategoryIdAndKeyword(String categoryId, String keyword) {
        categoryId = categoryId.trim().toLowerCase();
        keyword = keyword.trim().toLowerCase();
        List<WellnessResource> wellnessResourceList = _wellnessResourceRepository.findByCategoryIdAndKeyword(categoryId, keyword);
        return wellnessResourceList.stream()
                .map(this::mapToWellnessResourceResponse)
                .collect(Collectors.toList());
    }


    private WellnessResourceResponse mapToWellnessResourceResponse(WellnessResource wellnessResource) {
        return new WellnessResourceResponse(
                wellnessResource.getId(),
                wellnessResource.getTitle(),
                wellnessResource.getDescription(),
                wellnessResource.getUrl(),
                wellnessResource.getCategory().getId(),
                wellnessResource.getCategory().getTitle()
        );
    }
}
