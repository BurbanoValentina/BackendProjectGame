package com.example.gamebackend.patterns;

import com.example.gamebackend.model.Game;

public final class GameFactory {

    private GameFactory() {
        // Utility class
    }

    public static Game createGame(String playerName, String difficulty) {
        Game prototype = AbstractGameFactory.resolve(difficulty).createGame(playerName);
        return new GameBuilder()
            .setName(prototype.getPlayerName())
            .setGenre(prototype.getDifficulty())
            .setScore(prototype.getScore())
            .setCorrectAnswers(prototype.getCorrectAnswers())
            .setTotalQuestions(prototype.getTotalQuestions())
            .setDurationSeconds(prototype.getDurationSeconds())
            .build();
    }
}
