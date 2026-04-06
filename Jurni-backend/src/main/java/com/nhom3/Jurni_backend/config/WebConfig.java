package com.nhom3.Jurni_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadDir = System.getProperty("user.home") + "/jurni-uploads/";
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:" + uploadDir);
    }
}
