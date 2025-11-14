package com.example.gamebackend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.gamebackend.dto.AuthResponse;
import com.example.gamebackend.dto.LoginRequest;
import com.example.gamebackend.dto.RegisterRequest;
import com.example.gamebackend.dto.UserDTO;
import com.example.gamebackend.model.User;
import com.example.gamebackend.patterns.UserBuilder;
import com.example.gamebackend.repository.UserRepository;
import com.example.gamebackend.util.MD5Util;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // Utilidad MD5 ahora con métodos estáticos
    
    /**
     * Registra un nuevo usuario con validaciones y encriptación MD5
     * Usa Builder Pattern para construcción y validación
     */
    public AuthResponse register(RegisterRequest request) {
        try {
            // Validar si el usuario ya existe
            if (userRepository.existsByUsername(request.getUsername())) {
                return new AuthResponse(false, "El nombre de usuario ya está en uso");
            }
            
            if (userRepository.existsByNickname(request.getNickname())) {
                return new AuthResponse(false, "El apodo ya está en uso");
            }
            
            // Usar Builder Pattern para crear y validar el usuario
            UserBuilder builder = new UserBuilder();
            User user = builder
                    .setUsername(request.getUsername())
                    .setPassword(MD5Util.encrypt(request.getPassword())) // Encriptar con MD5
                    .setNickname(request.getNickname())
                    .build();
            
            // Guardar usuario
            User savedUser = userRepository.save(user);
            
            // Convertir a DTO (sin exponer la contraseña)
            UserDTO userDTO = convertToDTO(savedUser);
            
            return new AuthResponse(true, "Usuario registrado exitosamente", userDTO);
            
        } catch (IllegalArgumentException e) {
            return new AuthResponse(false, e.getMessage());
        } catch (Exception e) {
            return new AuthResponse(false, "Error al registrar el usuario: " + e.getMessage());
        }
    }
    
    /**
     * Autentica un usuario existente
     */
    public AuthResponse login(LoginRequest request) {
        try {
            // Buscar usuario por username
            Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
            
            if (userOptional.isEmpty()) {
                return new AuthResponse(false, "Usuario no encontrado");
            }
            
            User user = userOptional.get();
            
            // Verificar contraseña usando MD5
            String encryptedPassword = MD5Util.encrypt(request.getPassword());
            
            if (!user.getPassword().equals(encryptedPassword)) {
                return new AuthResponse(false, "Contraseña incorrecta");
            }
            
            // Login exitoso
            UserDTO userDTO = convertToDTO(user);
            return new AuthResponse(true, "Login exitoso", userDTO);
            
        } catch (Exception e) {
            return new AuthResponse(false, "Error al iniciar sesión: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza el high score de un usuario
     */
    public boolean updateHighScore(Long userId, Integer newScore) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (newScore > user.getHighScore()) {
                user.setHighScore(newScore);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Convierte User a UserDTO (sin exponer información sensible)
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
