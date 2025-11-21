package com.example.gamebackend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO used to create a new multiplayer room.
 */
public class CreateRoomRequest {
    
    @NotBlank(message = "Player id is required")
    private String playerId;
    
    @NotBlank(message = "Username is required")
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
