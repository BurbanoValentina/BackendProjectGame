package com.example.gamebackend.patterns;

import com.example.gamebackend.model.User;

/**
 * Builder Pattern para crear usuarios con validaciones
 */
public class UserBuilder {
    private String username;
    private String password;
    private String nickname;
    
    public UserBuilder setUsername(String username) {
        this.username = username;
        return this;
    }
    
    public UserBuilder setPassword(String password) {
        this.password = password;
        return this;
    }
    
    public UserBuilder setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }
    
    /**
     * Valida y construye el usuario
     * @return User construido
     * @throws IllegalArgumentException si las validaciones fallan
     */
    public User build() throws IllegalArgumentException {
        validateUsername();
        validatePassword();
        validateNickname();
        
        return new User(username, password, nickname);
    }
    
    private void validateUsername() {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío");
        }
        if (username.length() < 5) {
            throw new IllegalArgumentException("El nombre de usuario debe tener al menos 5 caracteres");
        }
        if (username.length() > 15) {
            throw new IllegalArgumentException("El nombre de usuario no puede tener más de 15 caracteres");
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("El nombre de usuario solo puede contener letras, números y guiones bajos");
        }
    }
    
    private void validatePassword() {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        if (password.length() < 5) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 5 caracteres");
        }
        if (password.length() > 15) {
            throw new IllegalArgumentException("La contraseña no puede tener más de 15 caracteres");
        }
    }
    
    private void validateNickname() {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("El apodo no puede estar vacío");
        }
        if (nickname.length() < 5) {
            throw new IllegalArgumentException("El apodo debe tener al menos 5 caracteres");
        }
        if (nickname.length() > 15) {
            throw new IllegalArgumentException("El apodo no puede tener más de 15 caracteres");
        }
    }
}
