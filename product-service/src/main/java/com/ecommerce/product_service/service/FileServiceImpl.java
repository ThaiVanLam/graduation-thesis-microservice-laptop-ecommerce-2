package com.ecommerce.product_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String randomId = UUID.randomUUID().toString();
        String fileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf(".")));

        Path configuredPath = Paths.get(path);

        if (!configuredPath.isAbsolute()) {
            Path baseDir = Paths.get("").toAbsolutePath();
            if (!baseDir.endsWith("product-service")) {
                Path productServiceDir = baseDir.resolve("product-service");
                if (Files.exists(productServiceDir)) {
                    baseDir = productServiceDir;
                }
            }
            configuredPath = baseDir.resolve(configuredPath).normalize();
        }

        Files.createDirectories(configuredPath);
        Path destinationFile = configuredPath.resolve(fileName);
        Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }
}
