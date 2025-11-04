package com.ecommerce.order_service.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProductSnapshot {
    private Long productId;
    private String productName;
    private String image;
    private Double price;
    private Double discount;
    private Double specialPrice;
}
