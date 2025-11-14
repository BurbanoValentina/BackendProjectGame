package com.example.gamebackend.patterns;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.gamebackend.model.Game;
import com.example.gamebackend.service.GameService;

@Component
public class GameFacade {

    private final GameService gameService;

    public GameFacade(GameService gameService) {
        this.gameService = gameService;
    }

    public Game startGameSession(String playerName, String difficulty) {
        return gameService.createGameSession(playerName, difficulty);
    }

    public Game updateGameSession(Integer id, int score, int correctAnswers, int totalQuestions, long durationSeconds) {
        return gameService.updateGameProgress(id, score, correctAnswers, totalQuestions, durationSeconds);
    }

    public List<Game> getLeaderboard() {
        return gameService.getAllGames();
    }

    public Game findGame(Integer id) {
        return gameService.getGameById(id);
    }
}
