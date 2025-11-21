package com.example.gamebackend.dto;

import jakarta.validation.constraints.NotBlank;

public class GameStartRequest {

    @NotBlank(message = "Player name is required")
    private String playerName;

    @NotBlank(message = "Difficulty is required")
    private String difficulty;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
