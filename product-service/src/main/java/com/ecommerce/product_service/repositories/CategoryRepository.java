package com.ecommerce.product_service.repositories;


import com.ecommerce.product_service.model.Category;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByCategoryName(@Size(min = 5, message = "at least 5 charactor long") @NotEmpty String categoryName);
}
