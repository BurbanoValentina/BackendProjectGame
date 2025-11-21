package com.example.gamebackend.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Document(collection = "games")
public class Game {

    @Id
    private String id;

    @NotBlank(message = "El nombre del jugador es obligatorio")
    @Field("player_name")
    private String playerName;

    @NotBlank(message = "La dificultad es obligatoria")
    @Field("difficulty")
    private String difficulty;

    @Min(value = 0, message = "El puntaje no puede ser negativo")
    @Field("score")
    private int score;

    @Min(value = 0, message = "Los aciertos no pueden ser negativos")
    @Field("correct_answers")
    private int correctAnswers;

    @Min(value = 0, message = "Las preguntas totales no pueden ser negativas")
    @Field("total_questions")
    private int totalQuestions;

    @Min(value = 0, message = "La duración no puede ser negativa")
    @Field("duration_seconds")
    private long durationSeconds;

    @CreatedDate
    @Field(value = "created_at")
    private LocalDateTime createdAt;

    public Game() {
        // Default constructor required by JPA
    }

    public Game(String playerName, String difficulty, int score, int correctAnswers, int totalQuestions, long durationSeconds) {
        this.playerName = playerName;
        this.difficulty = difficulty;
        this.score = score;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.durationSeconds = durationSeconds;
    }

    public Game(String id, String playerName, String difficulty) {
        this(playerName, difficulty, 0, 0, 0, 0);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Convenience getters/setters preserved for compatibility with patterns package.
     */
    public String getName() {
        return playerName;
    }

    public void setName(String name) {
        this.playerName = name;
    }

    public String getGenre() {
        return difficulty;
    }

    public void setGenre(String genre) {
        this.difficulty = genre;
    }

}
