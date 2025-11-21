package com.example.gamebackend.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.gamebackend.model.UserSession;
import com.example.gamebackend.repository.UserSessionRepository;

@Service
public class UserSessionService {

    private static final Duration SESSION_DURATION = Duration.ofHours(24);

    private final UserSessionRepository sessionRepository;

    public UserSessionService(UserSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public UserSession registerSession(String userId) {
        LocalDateTime now = LocalDateTime.now();
        closeExpiredSessionsForUser(userId);

        UserSession session = new UserSession();
        session.setUserId(userId);
        session.setSessionToken(UUID.randomUUID().toString());
        session.setCreatedAt(now);
        session.setExpiresAt(now.plus(SESSION_DURATION));
        session.setActive(true);
        session.setClosedReason(null);
        session.setClosedAt(null);

        return sessionRepository.save(session);
    }

    public void closeSession(String token, String reason) {
        sessionRepository.findBySessionToken(token).ifPresent(session -> closeSession(session, reason));
    }

    public boolean isSessionActive(String token) {
        return sessionRepository.findBySessionToken(token)
            .filter(UserSession::isActive)
            .filter(session -> session.getExpiresAt().isAfter(LocalDateTime.now()))
            .isPresent();
    }

    public void closeExpiredSessionsForUser(String userId) {
        List<UserSession> sessions = sessionRepository.findByUserIdAndActiveTrue(userId);
        LocalDateTime now = LocalDateTime.now();
        sessions.stream()
            .filter(session -> session.getExpiresAt().isBefore(now))
            .forEach(session -> closeSession(session, "Expired after 24h"));
    }

    @Scheduled(fixedDelayString = "${app.session.cleanup-interval-ms:3600000}")
    public void closeExpiredSessionsGlobally() {
        LocalDateTime now = LocalDateTime.now();
        List<UserSession> expired = sessionRepository.findByActiveTrueAndExpiresAtBefore(now);
        expired.forEach(session -> closeSession(session, "Expired after 24h"));
    }

    private void closeSession(UserSession session, String reason) {
        session.setActive(false);
        session.setClosedAt(LocalDateTime.now());
        session.setClosedReason(reason);
        sessionRepository.save(session);
    }
}
