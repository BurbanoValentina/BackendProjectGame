package com.example.gamebackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Propiedades personalizadas de la aplicación
 */
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    
    private Frontend frontend = new Frontend();
    
    public Frontend getFrontend() {
        return frontend;
    }
    
    public void setFrontend(Frontend frontend) {
        this.frontend = frontend;
    }
    
    public static class Frontend {
        private String url = "http://localhost:5173";
        
        public String getUrl() {
            return url;
        }
        
        public void setUrl(String url) {
            this.url = url;
        }
    }
}
