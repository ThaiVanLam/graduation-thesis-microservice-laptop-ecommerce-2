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

        // Normalize and validate data
        normalizeSpecification(specification);

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

    /**
     * Chuẩn hóa dữ liệu specifications
     */
    private void normalizeSpecification(ProductSpecification spec) {
        // Normalize CPU
        if (spec.getCpuBrand() != null) {
            spec.setCpuBrand(normalizeCpuBrand(spec.getCpuBrand()));
        }

        // Normalize RAM size
        if (spec.getRamSize() != null) {
            spec.setRamSize(normalizeMemorySize(spec.getRamSize()));
        }

        // Normalize Storage capacity
        if (spec.getStorageCapacity() != null) {
            spec.setStorageCapacity(normalizeMemorySize(spec.getStorageCapacity()));
        }

        // Normalize Display size
        if (spec.getDisplaySize() != null) {
            spec.setDisplaySize(normalizeDisplaySize(spec.getDisplaySize()));
        }

        // Normalize Weight
        if (spec.getWeight() != null) {
            spec.setWeight(normalizeWeight(spec.getWeight()));
        }

        // Normalize GPU VRAM
        if (spec.getGpuVram() != null) {
            spec.setGpuVram(normalizeMemorySize(spec.getGpuVram()));
        }
    }

    private String normalizeCpuBrand(String brand) {
        String normalized = brand.trim().toUpperCase();
        if (normalized.contains("INTEL")) return "Intel";
        if (normalized.contains("AMD")) return "AMD";
        if (normalized.contains("APPLE") || normalized.contains("M1") || normalized.contains("M2") || normalized.contains("M3")) return "Apple";
        return brand;
    }

    private String normalizeMemorySize(String size) {
        if (size == null || size.isBlank()) return size;

        String normalized = size.trim().toUpperCase();

        // Convert to GB if needed
        if (normalized.matches("\\d+\\s*MB")) {
            int mb = Integer.parseInt(normalized.replaceAll("[^0-9]", ""));
            if (mb >= 1024) {
                return (mb / 1024) + "GB";
            }
        }

        // Ensure GB format
        if (normalized.matches("\\d+\\s*G.*")) {
            return normalized.replaceAll("\\s+", "").replaceAll("GB?.*", "GB");
        }

        // Ensure TB format
        if (normalized.matches("\\d+\\s*T.*")) {
            return normalized.replaceAll("\\s+", "").replaceAll("TB?.*", "TB");
        }

        return size;
    }

    private String normalizeDisplaySize(String size) {
        if (size == null || size.isBlank()) return size;

        String normalized = size.trim();

        // Extract number and add inches symbol
        if (normalized.matches("\\d+\\.?\\d*")) {
            return normalized + "\"";
        }

        if (normalized.matches("\\d+\\.?\\d*\\s*(inch|inches|in|')")) {
            return normalized.replaceAll("\\s*(inch|inches|in|')", "\"");
        }

        return size;
    }

    private String normalizeWeight(String weight) {
        if (weight == null || weight.isBlank()) return weight;

        String normalized = weight.trim().toLowerCase();

        // Convert to kg if needed
        if (normalized.matches("\\d+\\.?\\d*\\s*g(ram)?s?")) {
            double grams = Double.parseDouble(normalized.replaceAll("[^0-9.]", ""));
            return String.format("%.2fkg", grams / 1000);
        }

        // Ensure kg format
        if (normalized.matches("\\d+\\.?\\d*\\s*k.*")) {
            return normalized.replaceAll("\\s+", "").replaceAll("kg?.*", "kg");
        }

        return weight;
    }
}