package com.ecommerce.order_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "productId", column = @Column(name = "product_id")),
            @AttributeOverride(name = "productName", column = @Column(name = "product_name")),
            @AttributeOverride(name = "image", column = @Column(name = "product_image")),
            @AttributeOverride(name = "price", column = @Column(name = "product_price")),
            @AttributeOverride(name = "discount", column = @Column(name = "product_discount")),
            @AttributeOverride(name = "specialPrice", column = @Column(name = "product_special_price"))
    })
    private ProductSnapshot productSnapshot;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    private Integer quantity;
    @Column(name = "item_discount")
    private Double discount;
    private Double orderedProductPrice;
}
