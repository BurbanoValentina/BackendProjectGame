package com.example.gamebackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.gamebackend.dto.AuthResponse;
import com.example.gamebackend.dto.LoginRequest;
import com.example.gamebackend.dto.PasswordChangeRequest;
import com.example.gamebackend.dto.RegisterRequest;
import com.example.gamebackend.service.UserService;
import com.example.gamebackend.service.UserSessionService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private UserSessionService sessionService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = userService.register(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
        @PutMapping("/user/{userId}/highscore")
        public ResponseEntity<String> updateHighScore(
            @PathVariable String userId,
            @RequestParam Integer score) {
        boolean updated = userService.updateHighScore(userId, score);
        
        if (updated) {
            return ResponseEntity.ok("High score updated");
        } else {
            return ResponseEntity.badRequest().body("Unable to update the high score");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String token) {
        sessionService.closeSession(token, "Manual logout");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/password/change")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeRequest request) {
        boolean updated = userService.changePassword(request);
        if (updated) {
            return ResponseEntity.ok("Password updated successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with the provided identifier");
    }
}
