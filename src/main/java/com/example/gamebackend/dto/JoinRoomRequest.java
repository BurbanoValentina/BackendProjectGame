package com.example.gamebackend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object used to join a multiplayer room.
 */
public class JoinRoomRequest {
    
    @NotBlank(message = "Room code is required")
    private String roomCode;
    
    @NotBlank(message = "Player id is required")
    private String playerId;
    
    @NotBlank(message = "Username is required")
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
