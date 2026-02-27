package com.ecommerce.product_service.repositories;

import com.ecommerce.product_service.model.ProductSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductSpecificationRepository extends JpaRepository<ProductSpecification, Long> {
    Optional<ProductSpecification> findByProductProductId(Long productId);
    void deleteByProductProductId(Long productId);
}