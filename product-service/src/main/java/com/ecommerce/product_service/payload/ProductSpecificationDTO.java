package com.ecommerce.product_service.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSpecificationDTO {

    // CPU
    private String cpuBrand;
    private String cpuModel;
    private String cpuGeneration;
    private Integer cpuCores;
    private Integer cpuThreads;
    private String cpuBaseClock;
    private String cpuBoostClock;

    // RAM
    private String ramSize;
    private String ramType;
    private String ramSpeed;
    private Integer ramSlots;
    private String ramMaxUpgrade;

    // Storage
    private String storageType;
    private String storageCapacity;
    private String storageInterface;
    private Integer additionalStorageSlots;

    // Display
    private String displaySize;
    private String displayResolution;
    private String displayPanelType;
    private String displayRefreshRate;
    private String displayBrightness;
    private String displayColorGamut;
    private Boolean displayTouchScreen;

    // GPU
    private String gpuType;
    private String gpuBrand;
    private String gpuModel;
    private String gpuVram;

    // Battery
    private String batteryCapacity;
    private String batteryType;
    private String batteryLife;
    private Boolean fastCharging;
    private String chargerWattage;

    // Physical
    private String weight;
    private String dimensions;
    private String material;
    private String color;

    // OS
    private String os;
    private String osVersion;

    // Additional Features
    private Boolean keyboardBacklight;
    private Boolean fingerprintReader;
    private String webcamResolution;
    private String audioSystem;
    private String connectivity;
    private String ports;
    private String warranty;
}