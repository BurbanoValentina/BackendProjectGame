package com.example.gamebackend.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    private static final String[] DEFAULT_METHODS = {"GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"};

    private final AppProperties appProperties;

    public CorsConfig(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                List<String> origins = appProperties.getFrontend().getAllowedOrigins();
                registry.addMapping("/**")
                    .allowedOrigins(origins.toArray(new String[0]))
                    .allowedMethods(DEFAULT_METHODS)
                    .allowedHeaders("*")
                    .exposedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
            }
        };
    }
}
