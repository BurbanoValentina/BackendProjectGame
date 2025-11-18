package com.example.gamebackend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para unirse a una sala multijugador
 */
public class JoinRoomRequest {
    
    @NotBlank(message = "El código de sala es requerido")
    private String roomCode;
    
    @NotBlank(message = "El ID del jugador es requerido")
    private String playerId;
    
    @NotBlank(message = "El nombre de usuario es requerido")
    private String username;

    public JoinRoomRequest() {
    }

    public JoinRoomRequest(String roomCode, String playerId, String username) {
        this.roomCode = roomCode;
        this.playerId = playerId;
        this.username = username;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
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
