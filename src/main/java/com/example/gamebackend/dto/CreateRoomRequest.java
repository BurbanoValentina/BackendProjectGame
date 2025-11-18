package com.example.gamebackend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para crear una sala multijugador
 */
public class CreateRoomRequest {
    
    @NotBlank(message = "El ID del jugador es requerido")
    private String playerId;
    
    @NotBlank(message = "El nombre de usuario es requerido")
    private String username;

    public CreateRoomRequest() {
    }

    public CreateRoomRequest(String playerId, String username) {
        this.playerId = playerId;
        this.username = username;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
