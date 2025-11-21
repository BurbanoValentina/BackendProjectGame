package com.example.gamebackend.patterns;

import com.example.gamebackend.model.Game;

/**
 * Prototype Pattern: clones Game instances for scenario testing.
 */
public class PrototypeGame implements Cloneable {
    private final Game game;

    public PrototypeGame(Game game) {
        this.game = game;
    }

    public Game cloneGame() {
        Game clone = new Game();
        clone.setName(game.getName());
        clone.setGenre(game.getGenre());
        clone.setScore(game.getScore());
        clone.setCorrectAnswers(game.getCorrectAnswers());
        clone.setTotalQuestions(game.getTotalQuestions());
        clone.setDurationSeconds(game.getDurationSeconds());
        clone.setCreatedAt(game.getCreatedAt());
        return clone;
    }
}
