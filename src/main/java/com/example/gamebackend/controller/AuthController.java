package com.example.gamebackend.controller;

import com.example.gamebackend.dto.*;
import com.example.gamebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * Endpoint para registrar un nuevo usuario
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = userService.register(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Endpoint para iniciar sesión
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    /**
     * Endpoint para actualizar el high score
     */
    @PutMapping("/user/{userId}/highscore")
    public ResponseEntity<String> updateHighScore(
            @PathVariable Long userId,
            @RequestParam Integer score) {
        boolean updated = userService.updateHighScore(userId, score);
        
        if (updated) {
            return ResponseEntity.ok("High score actualizado");
        } else {
            return ResponseEntity.badRequest().body("No se pudo actualizar el high score");
        }
    }
}
