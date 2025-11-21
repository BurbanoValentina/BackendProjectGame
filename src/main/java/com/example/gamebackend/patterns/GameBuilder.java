package com.example.gamebackend.patterns;

import com.example.gamebackend.model.Game;

public class GameBuilder {
    private String id;
    private String playerName;
    private String difficulty;
    private int score;
    private int correctAnswers;
    private int totalQuestions;
    private long durationSeconds;

    public GameBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public GameBuilder setName(String name) {
        this.playerName = name;
        return this;
    }

    public GameBuilder setGenre(String genre) {
        this.difficulty = genre;
        return this;
    }

    public GameBuilder setScore(int score) {
        this.score = score;
        return this;
    }

    public GameBuilder setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
        return this;
    }

    public GameBuilder setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
        return this;
    }

    public GameBuilder setDurationSeconds(long durationSeconds) {
        this.durationSeconds = durationSeconds;
        return this;
    }

    public Game build() {
        Game game = new Game(playerName, difficulty, score, correctAnswers, totalQuestions, durationSeconds);
        if (id != null) {
            game.setId(id);
        }
        return game;
    }
}
