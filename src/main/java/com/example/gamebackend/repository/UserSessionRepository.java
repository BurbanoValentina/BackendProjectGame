package com.example.gamebackend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.gamebackend.model.UserSession;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, String> {
    Optional<UserSession> findBySessionToken(String token);
    List<UserSession> findByUserIdAndActiveTrue(String userId);
    List<UserSession> findByActiveTrueAndExpiresAtBefore(LocalDateTime reference);
}
