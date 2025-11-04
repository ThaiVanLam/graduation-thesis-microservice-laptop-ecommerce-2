package com.ecommerce.order_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "cart_items")
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "discount", column = @Column(name = "product_discount"))
    })
    private ProductSnapshot productSnapshot;

    private Integer quantity;
    private Double discount;
    private Double productPrice;
}
