package com.ecommerce.order_service.client;

import com.ecommerce.order_service.payload.ProductDTO;

public interface ProductServiceClient {
    ProductDTO getProductById(Long productId);

    void reduceProductQuantity(Long productId, int quantity);
}
