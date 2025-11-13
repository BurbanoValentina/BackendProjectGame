package com.example.gamebackend.patterns;

import com.example.gamebackend.model.Game;

public class GameFactory {
    public static Game createGame(String playerName, String difficulty) {
        return new Game(playerName, difficulty, 0, 0, 0, 0);
    }
}
