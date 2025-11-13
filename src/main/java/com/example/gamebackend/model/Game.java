package com.example.gamebackend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "game_results")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre del jugador es obligatorio")
    @Column(name = "player_name", nullable = false)
    private String playerName;

    @NotBlank(message = "La dificultad es obligatoria")
    @Column(name = "difficulty", nullable = false)
    private String difficulty;

    @Min(value = 0, message = "El puntaje no puede ser negativo")
    @Column(name = "score")
    private int score;

    @Min(value = 0, message = "Los aciertos no pueden ser negativos")
    @Column(name = "correct_answers")
    private int correctAnswers;

    @Min(value = 0, message = "Las preguntas totales no pueden ser negativas")
    @Column(name = "total_questions")
    private int totalQuestions;

    @Min(value = 0, message = "La duración no puede ser negativa")
    @Column(name = "duration_seconds")
    private long durationSeconds;

    @Column(name = "created_at", nullable = false, updatable = false)
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

    public Game(Integer id, String playerName, String difficulty) {
        this(playerName, difficulty, 0, 0, 0, 0);
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
