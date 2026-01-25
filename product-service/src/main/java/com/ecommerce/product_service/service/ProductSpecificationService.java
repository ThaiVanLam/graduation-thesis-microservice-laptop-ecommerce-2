package com.ecommerce.product_service.service;

import com.ecommerce.product_service.payload.ProductSpecificationDTO;

public interface ProductSpecificationService {
    ProductSpecificationDTO createOrUpdateSpecification(Long productId, ProductSpecificationDTO specDTO);
    ProductSpecificationDTO getSpecificationByProductId(Long productId);
    void deleteSpecification(Long productId);
}