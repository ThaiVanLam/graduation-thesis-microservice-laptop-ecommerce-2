package com.ecommerce.product_service.service;

import com.ecommerce.product_service.payload.AnalyticsProductResponse;
import com.ecommerce.product_service.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public AnalyticsProductResponse getAnalyticsData() {
        AnalyticsProductResponse response = new AnalyticsProductResponse();

        long productCount = productRepository.count();

        response.setProductCount(String.valueOf(productCount));
        return response;
    }
}
