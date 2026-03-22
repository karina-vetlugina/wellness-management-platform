package ca.gbc.comp3095.wellnessservice.repository;

import ca.gbc.comp3095.wellnessservice.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

}
