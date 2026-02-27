package com.ecommerce.product_service.imageutil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ImagePathUtils {

    private ImagePathUtils() {
    }

    public static Path resolveConfiguredPath(String configuredDirectory) {
        Path configuredPath = Paths.get(configuredDirectory);

        if (configuredPath.isAbsolute()) {
            return configuredPath.normalize();
        }

        Path baseDir = Paths.get("").toAbsolutePath();
        if (!baseDir.endsWith("product-service")) {
            Path productServiceDir = baseDir.resolve("product-service");
            if (Files.exists(productServiceDir)) {
                baseDir = productServiceDir;
            }
        }

        return baseDir.resolve(configuredPath).normalize();
    }
}