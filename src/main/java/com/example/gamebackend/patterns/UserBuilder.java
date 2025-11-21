package com.example.gamebackend.patterns;

import com.example.gamebackend.model.User;

/**
 * Builder Pattern: constructs users with inline validation before persistence.
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
    
    public User build() throws IllegalArgumentException {
        validateUsername();
        validatePassword();
        validateNickname();
        
        return new User(username, password, nickname);
    }
    
    private void validateUsername() {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (username.length() < 5) {
            throw new IllegalArgumentException("Username must contain at least 5 characters");
        }
        if (username.length() > 15) {
            throw new IllegalArgumentException("Username cannot exceed 15 characters");
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Username can only contain letters, digits, and underscores");
        }
    }
    
    private void validatePassword() {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (password.length() < 5) {
            throw new IllegalArgumentException("Password must contain at least 5 characters");
        }
        if (password.length() > 15) {
            throw new IllegalArgumentException("Password cannot exceed 15 characters");
        }
    }
    
    private void validateNickname() {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("Nickname cannot be empty");
        }
        if (nickname.length() < 5) {
            throw new IllegalArgumentException("Nickname must contain at least 5 characters");
        }
        if (nickname.length() > 15) {
            throw new IllegalArgumentException("Nickname cannot exceed 15 characters");
        }
    }
}
