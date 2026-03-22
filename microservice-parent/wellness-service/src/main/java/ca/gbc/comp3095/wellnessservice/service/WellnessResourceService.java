package ca.gbc.comp3095.wellnessservice.service;

import ca.gbc.comp3095.wellnessservice.dto.WellnessResourceRequest;
import ca.gbc.comp3095.wellnessservice.dto.WellnessResourceResponse;

import java.util.List;

public interface WellnessResourceService {
    WellnessResourceResponse createResource(WellnessResourceRequest wellnessResourceRequest);
    List<WellnessResourceResponse> findAll();
    String updateResource(String resourceId, WellnessResourceRequest wellnessResourceRequest);
    boolean deleteResource(String resourceId);
    WellnessResourceResponse getResourceById(String resourceId);
    List<WellnessResourceResponse> findByCategoryId(String categoryId);
    List<WellnessResourceResponse> findByKeyword(String keyword);
    List<WellnessResourceResponse> findByCategoryIdAndKeyword(String categoryId, String keyword);

}
