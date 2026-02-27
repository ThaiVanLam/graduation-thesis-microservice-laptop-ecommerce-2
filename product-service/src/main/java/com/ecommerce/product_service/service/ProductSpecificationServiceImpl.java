package com.ecommerce.product_service.service;

import com.ecommerce.product_service.exceptions.ResourceNotFoundException;
import com.ecommerce.product_service.model.Product;
import com.ecommerce.product_service.model.ProductSpecification;
import com.ecommerce.product_service.payload.ProductSpecificationDTO;
import com.ecommerce.product_service.repositories.ProductRepository;
import com.ecommerce.product_service.repositories.ProductSpecificationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductSpecificationServiceImpl implements ProductSpecificationService {

    @Autowired
    private ProductSpecificationRepository specificationRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public ProductSpecificationDTO createOrUpdateSpecification(Long productId, ProductSpecificationDTO specDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        ProductSpecification specification = specificationRepository.findByProductProductId(productId)
                .orElse(new ProductSpecification());

        // Map DTO to entity
        modelMapper.map(specDTO, specification);
        specification.setProduct(product);

        ProductSpecification savedSpec = specificationRepository.save(specification);
        return modelMapper.map(savedSpec, ProductSpecificationDTO.class);
    }

    @Override
    public ProductSpecificationDTO getSpecificationByProductId(Long productId) {
        ProductSpecification specification = specificationRepository.findByProductProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Specification", "productId", productId));

        return modelMapper.map(specification, ProductSpecificationDTO.class);
    }

    @Override
    @Transactional
    public void deleteSpecification(Long productId) {
        specificationRepository.deleteByProductProductId(productId);
    }
}