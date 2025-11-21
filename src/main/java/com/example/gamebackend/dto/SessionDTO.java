package com.example.gamebackend.dto;

import java.time.LocalDateTime;

public class SessionDTO {
    private String sessionId;
    private String token;
    private LocalDateTime expiresAt;

    public SessionDTO(String sessionId, String token, LocalDateTime expiresAt) {
        this.sessionId = sessionId;
        this.token = token;
        this.expiresAt = expiresAt;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
