package com.pathz.dbconcurrency.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Дозволити на всі ендпоінти
                .allowedOrigins("*") // Дозволити з будь-якого джерела (HTML файл, localhost:3000, тощо)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Дозволити всі методи
                .allowedHeaders("*"); // Дозволити всі заголовки
    }
}