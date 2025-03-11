package com.springboot.dev_spring_boot_demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map URL /img/** to the local directory ./img/
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:./img/");

        // Map URL /profile_pictures/** to the local directory ./profile_pictures/
        registry.addResourceHandler("/profile_pictures/**")
                .addResourceLocations("file:./profile_pictures/");
    }
}
