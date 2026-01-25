package com.ecommerce.product_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_specifications")
public class ProductSpecification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id", unique = true)
    private Product product;

    // CPU (Processor)
    @Column(name = "processor")
    private String processor; // Intel Core i7-13700H, AMD Ryzen 7 7735HS, Apple M3

    // RAM
    @Column(name = "ram")
    private String ram; // 16GB DDR5, 32GB LPDDR5

    // Storage
    @Column(name = "storage")
    private String storage; // 512GB SSD NVMe, 1TB SSD PCIe Gen 4

    // Display
    @Column(name = "display")
    private String display; // 15.6" FHD IPS 144Hz, 14" 2K OLED 60Hz

    // Graphics (GPU)
    @Column(name = "graphics")
    private String graphics; // NVIDIA RTX 4060 8GB, AMD Radeon RX 7600M, Integrated Intel Iris Xe
}