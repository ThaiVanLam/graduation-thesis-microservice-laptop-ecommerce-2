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

    // CPU Specifications
    @Column(name = "cpu_brand")
    private String cpuBrand; // Intel, AMD, Apple

    @Column(name = "cpu_model")
    private String cpuModel; // Core i7-13700H, Ryzen 7 7735HS, M3

    @Column(name = "cpu_generation")
    private String cpuGeneration; // 13th Gen, Zen 4

    @Column(name = "cpu_cores")
    private Integer cpuCores;

    @Column(name = "cpu_threads")
    private Integer cpuThreads;

    @Column(name = "cpu_base_clock")
    private String cpuBaseClock; // 2.3 GHz

    @Column(name = "cpu_boost_clock")
    private String cpuBoostClock; // 5.0 GHz

    // RAM Specifications
    @Column(name = "ram_size")
    private String ramSize; // 16GB, 32GB

    @Column(name = "ram_type")
    private String ramType; // DDR5, DDR4, LPDDR5

    @Column(name = "ram_speed")
    private String ramSpeed; // 4800MHz, 5200MHz

    @Column(name = "ram_slots")
    private Integer ramSlots; // Số khe RAM

    @Column(name = "ram_max_upgrade")
    private String ramMaxUpgrade; // 64GB

    // Storage Specifications
    @Column(name = "storage_type")
    private String storageType; // SSD NVMe, SSD SATA

    @Column(name = "storage_capacity")
    private String storageCapacity; // 512GB, 1TB

    @Column(name = "storage_interface")
    private String storageInterface; // PCIe Gen 4, PCIe Gen 3

    @Column(name = "additional_storage_slots")
    private Integer additionalStorageSlots;

    // Display Specifications
    @Column(name = "display_size")
    private String displaySize; // 15.6", 14"

    @Column(name = "display_resolution")
    private String displayResolution; // 1920x1080, 2560x1600

    @Column(name = "display_panel_type")
    private String displayPanelType; // IPS, OLED, TN

    @Column(name = "display_refresh_rate")
    private String displayRefreshRate; // 60Hz, 144Hz, 165Hz

    @Column(name = "display_brightness")
    private String displayBrightness; // 300 nits, 500 nits

    @Column(name = "display_color_gamut")
    private String displayColorGamut; // 100% sRGB, 100% DCI-P3

    @Column(name = "display_touch_screen")
    private Boolean displayTouchScreen;

    // GPU Specifications
    @Column(name = "gpu_type")
    private String gpuType; // Integrated, Dedicated

    @Column(name = "gpu_brand")
    private String gpuBrand; // NVIDIA, AMD, Intel, Apple

    @Column(name = "gpu_model")
    private String gpuModel; // RTX 4060, Radeon RX 7600M

    @Column(name = "gpu_vram")
    private String gpuVram; // 8GB GDDR6

    // Battery Specifications
    @Column(name = "battery_capacity")
    private String batteryCapacity; // 80Wh, 56Wh

    @Column(name = "battery_type")
    private String batteryType; // Li-ion, Li-Polymer

    @Column(name = "battery_life")
    private String batteryLife; // Up to 10 hours

    @Column(name = "fast_charging")
    private Boolean fastCharging;

    @Column(name = "charger_wattage")
    private String chargerWattage; // 65W, 135W

    // Physical Specifications
    @Column(name = "weight")
    private String weight; // 1.8kg, 2.2kg

    @Column(name = "dimensions")
    private String dimensions; // 356 x 234 x 18 mm

    @Column(name = "material")
    private String material; // Aluminum, Plastic, Magnesium Alloy

    @Column(name = "color")
    private String color; // Silver, Space Gray, Black

    // Operating System
    @Column(name = "os")
    private String os; // Windows 11, macOS Sonoma, Linux

    @Column(name = "os_version")
    private String osVersion;

    // Additional Features
    @Column(name = "keyboard_backlight")
    private Boolean keyboardBacklight;

    @Column(name = "fingerprint_reader")
    private Boolean fingerprintReader;

    @Column(name = "webcam_resolution")
    private String webcamResolution; // 720p, 1080p

    @Column(name = "audio_system")
    private String audioSystem; // Stereo, Dolby Atmos

    @Column(name = "connectivity")
    private String connectivity; // WiFi 6E, Bluetooth 5.3

    @Column(name = "ports", length = 500)
    private String ports; // USB-C, HDMI, etc.

    @Column(name = "warranty")
    private String warranty; // 1 year, 2 years
}