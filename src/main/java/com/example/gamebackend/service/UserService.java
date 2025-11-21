package com.example.gamebackend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.gamebackend.dto.AuthResponse;
import com.example.gamebackend.dto.LoginRequest;
import com.example.gamebackend.dto.PasswordChangeRequest;
import com.example.gamebackend.dto.RegisterRequest;
import com.example.gamebackend.dto.SessionDTO;
import com.example.gamebackend.dto.UserDTO;
import com.example.gamebackend.model.User;
import com.example.gamebackend.model.UserSession;
import com.example.gamebackend.patterns.UserBuilder;
import com.example.gamebackend.repository.UserRepository;
import com.example.gamebackend.util.MD5Util;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionService sessionService;

    /**
     * Registers a new user using the Builder Pattern and MD5 hashing.
     */
    public AuthResponse register(RegisterRequest request) {
        try {
            // Validate username and nickname uniqueness
            if (userRepository.existsByUsername(request.getUsername())) {
                return new AuthResponse(false, "Username is already taken");
            }
            
            if (userRepository.existsByNickname(request.getNickname())) {
                return new AuthResponse(false, "Nickname is already taken");
            }
            
            // Builder Pattern performs validation before hashing the password
                UserBuilder builder = new UserBuilder();
                // Validate the password while it is still in plain text
                User user = builder
                    .setUsername(request.getUsername())
                    .setPassword(request.getPassword())
                    .setNickname(request.getNickname())
                    .build();
                // Encrypt the password only after it passes Builder validation
                user.setPassword(MD5Util.encrypt(user.getPassword()));
            
            // Guardar usuario
            User savedUser = userRepository.save(user);
            
            // Convert entity to DTO without exposing password hashes
            UserDTO userDTO = convertToDTO(savedUser);
            
            UserSession session = sessionService.registerSession(savedUser.getId());
            SessionDTO sessionDTO = new SessionDTO(session.getId(), session.getSessionToken(), session.getExpiresAt());
            return new AuthResponse(true, "User registered successfully", userDTO, sessionDTO);
            
        } catch (IllegalArgumentException e) {
            return new AuthResponse(false, e.getMessage());
        } catch (Exception e) {
            return new AuthResponse(false, "Unable to register the user: " + e.getMessage());
        }
    }
    
    /**
     * Authenticates an existing user.
     */
    public AuthResponse login(LoginRequest request) {
        try {
            // Look up user by username
            Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
            
            if (userOptional.isEmpty()) {
                return new AuthResponse(false, "User not found");
            }
            
            User user = userOptional.get();
            
            // Verify password using MD5 hash comparison
            String encryptedPassword = MD5Util.encrypt(request.getPassword());
            
            if (!user.getPassword().equals(encryptedPassword)) {
                return new AuthResponse(false, "Invalid credentials");
            }
            
            // Login exitoso
            UserDTO userDTO = convertToDTO(user);
            sessionService.closeExpiredSessionsForUser(user.getId());
            UserSession session = sessionService.registerSession(user.getId());
            SessionDTO sessionDTO = new SessionDTO(session.getId(), session.getSessionToken(), session.getExpiresAt());
            return new AuthResponse(true, "Login successful", userDTO, sessionDTO);
            
        } catch (Exception e) {
            return new AuthResponse(false, "Unable to complete login: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza el high score de un usuario
     */
    public boolean updateHighScore(String userId, Integer newScore) {
        if (userId == null || newScore == null) {
            return false;
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Integer storedHighScore = user.getHighScore();
            int currentHighScore = storedHighScore != null ? storedHighScore : 0;
            if (newScore > currentHighScore) {
                user.setHighScore(newScore);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    public boolean changePassword(PasswordChangeRequest request) {
        Optional<User> userOptional = userRepository.findByUsername(request.getIdentifier());

        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByNickname(request.getIdentifier());
        }

        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();
        UserBuilder builder = new UserBuilder();
        builder.setUsername(user.getUsername())
               .setNickname(user.getNickname())
               .setPassword(request.getNewPassword())
               .build();

        user.setPassword(MD5Util.encrypt(request.getNewPassword()));
        userRepository.save(user);
        sessionService.closeExpiredSessionsForUser(user.getId());
        return true;
    }
    
    /**
     * Maps User entities to DTOs without exposing sensitive information.
     */
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getHighScore()
        );
    }
}
