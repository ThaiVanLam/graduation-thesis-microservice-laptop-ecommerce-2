package com.ecommerce.order_service.client;

import com.ecommerce.order_service.exceptions.ResourceNotFoundException;
import com.ecommerce.order_service.clientpayload.ProductDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestTemplateProductServiceClient implements ProductServiceClient{
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public RestTemplateProductServiceClient(RestTemplate restTemplate,
                                            @Value("${product.service.base-url:http://localhost:8081/api}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    @Override
    public ProductDTO getProductById(Long productId) {
        ResponseEntity<ProductDTO> response = restTemplate.getForEntity(baseUrl + "/internal/products/" + productId, ProductDTO.class);
        ProductDTO body = response.getBody();
        if (body == null) {
            throw new ResourceNotFoundException("Product", "productId", productId);
        }
        return body;
    }

    @Override
    public void reduceProductQuantity(Long productId, int quantity) {
        restTemplate.exchange(baseUrl + "/internal/products/" + productId + "/reduce-stock", HttpMethod.POST, new HttpEntity<>(new InventoryUpdateRequest(quantity)), Void.class);
    }

    private static class InventoryUpdateRequest {
        private final int quantity;

        private InventoryUpdateRequest(int quantity) {
            this.quantity = quantity;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
