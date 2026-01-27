package com.ecommerce.product_service.util;

import java.util.Random;

public class SKUGenerator {

    public static String generateSKU(String categoryName, String brand, String productName) {
        // Extract category prefix (first 3 letters uppercase)
        String categoryPrefix = extractPrefix(categoryName, 3);

        // Extract brand (uppercase)
        String brandPrefix = brand.toUpperCase().replaceAll("[^A-Z0-9]", "");

        // Extract product identifier from name
        String productPrefix = extractProductPrefix(productName);

        // Generate random 6-digit number
        String randomNumber = String.format("%06d", new Random().nextInt(1000000));

        // Combine: CATEGORY-BRAND-PRODUCT-RANDOM
        return String.format("%s-%s-%s-%s",
                categoryPrefix,
                brandPrefix,
                productPrefix,
                randomNumber
        );
    }

    private static String extractPrefix(String text, int length) {
        if (text == null || text.isEmpty()) {
            return "XXX";
        }
        String cleaned = text.toUpperCase()
                .replaceAll("[^A-Z]", "")
                .replaceAll("\\s+", "");

        return cleaned.length() >= length
                ? cleaned.substring(0, length)
                : cleaned + "X".repeat(length - cleaned.length());
    }

    private static String extractProductPrefix(String productName) {
        if (productName == null || productName.isEmpty()) {
            return "XXX";
        }

        // Try to extract model name (e.g., "XPS" from "Dell XPS 13")
        String[] words = productName.split("\\s+");
        for (String word : words) {
            // Look for alphanumeric words (model names)
            if (word.matches("^[A-Za-z0-9]+$") && word.length() >= 2) {
                String cleaned = word.toUpperCase().replaceAll("[^A-Z0-9]", "");
                return cleaned.length() > 5
                        ? cleaned.substring(0, 5)
                        : cleaned;
            }
        }

        // Fallback: use first 3 characters
        return extractPrefix(productName, 3);
    }
}