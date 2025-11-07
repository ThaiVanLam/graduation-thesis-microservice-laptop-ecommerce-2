package com.ecommerce.product_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Value;import java.nio.file.Path;
import static com.ecommerce.product_service.util.ImagePathUtils.resolveConfiguredPath;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${project.image}")
    private String imageDirectory;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path resolvedDirectory = resolveConfiguredPath(imageDirectory);
        String resourceLocation = resolvedDirectory.toUri().toString();
        registry.addResourceHandler("/images/**").addResourceLocations(resourceLocation);
    }
}
