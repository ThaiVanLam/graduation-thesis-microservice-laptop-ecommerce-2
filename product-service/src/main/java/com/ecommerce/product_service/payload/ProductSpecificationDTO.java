package com.ecommerce.product_service.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSpecificationDTO {

    @Schema(description = "Processor/CPU", example = "Intel Core i7-13700H (up to 5.0GHz, 14 cores)")
    private String processor;

    @Schema(description = "RAM", example = "16GB DDR5 4800MHz")
    private String ram;

    @Schema(description = "Storage", example = "512GB SSD NVMe PCIe Gen 4")
    private String storage;

    @Schema(description = "Display", example = "15.6 inch FHD (1920x1080) IPS 144Hz")
    private String display;

    @Schema(description = "Graphics Card", example = "NVIDIA GeForce RTX 4060 8GB GDDR6")
    private String graphics;
}