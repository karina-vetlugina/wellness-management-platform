package ca.gbc.comp3095.wellnessservice.repository;


import ca.gbc.comp3095.wellnessservice.model.WellnessResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WellnessResourceRepository extends JpaRepository<WellnessResource, String> {

    List<WellnessResource> findByCategoryId(String categoryId);

    @Query("SELECT wr FROM WellnessResource wr WHERE LOWER(wr.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(wr.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<WellnessResource> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT wr FROM WellnessResource wr WHERE wr.category.id = :categoryId AND (LOWER(wr.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(wr.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<WellnessResource> findByCategoryIdAndKeyword(@Param("categoryId") String categoryId, @Param("keyword") String keyword);
}
