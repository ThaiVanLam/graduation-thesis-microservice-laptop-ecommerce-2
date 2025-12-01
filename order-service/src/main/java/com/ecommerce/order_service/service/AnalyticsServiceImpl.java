package com.ecommerce.order_service.service;

import com.ecommerce.order_service.payload.AnalyticsOrderResponse;
import com.ecommerce.order_service.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public AnalyticsOrderResponse getAnalyticsData() {
        AnalyticsOrderResponse response = new AnalyticsOrderResponse();

        long totalOrders = orderRepository.count();;
        Double totalRevenue = orderRepository.getTotalRevenue();

        response.setTotalOrders(String.valueOf(totalOrders));
        response.setTotalRevenue(String.valueOf(totalRevenue != null ? totalRevenue : 0));
        return response;
    }
}
