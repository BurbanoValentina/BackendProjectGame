package com.example.gamebackend.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    
    private Frontend frontend = new Frontend();
    private Session session = new Session();
    
    public Frontend getFrontend() {
        return frontend;
    }
    
    public void setFrontend(Frontend frontend) {
        this.frontend = frontend;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
    
    public static class Frontend {
        private String url = "https://frotendproject.vercel.app/";
        private List<String> allowedOrigins = new ArrayList<>(List.of("https://frotendproject.vercel.app/"));
        
        public String getUrl() {
            return url;
        }
        
        public void setUrl(String url) {
            this.url = url;
        }

        public List<String> getAllowedOrigins() {
            if (allowedOrigins == null || allowedOrigins.isEmpty()) {
                return List.of(url != null ? url : "https://frotendproject.vercel.app/");
            }
            return allowedOrigins;
        }

        public void setAllowedOrigins(List<String> allowedOrigins) {
            if (allowedOrigins == null) {
                this.allowedOrigins = List.of(url != null ? url : "https://frotendproject.vercel.app/");
                return;
            }
            this.allowedOrigins = allowedOrigins.stream()
                .filter(origin -> origin != null && !origin.isBlank())
                .map(String::trim)
                .toList();
        }
    }

    public static class Session {
        private long cleanupIntervalMs = 900_000L;

        public long getCleanupIntervalMs() {
            return cleanupIntervalMs;
        }

        public void setCleanupIntervalMs(long cleanupIntervalMs) {
            this.cleanupIntervalMs = cleanupIntervalMs;
        }
    }
}
