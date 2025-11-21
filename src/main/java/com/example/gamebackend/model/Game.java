package com.example.gamebackend.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @Column(length = 36)
    private String id;

    @NotBlank(message = "Player name is required")
    @Column(name = "player_name", nullable = false)
    private String playerName;

    @NotBlank(message = "Difficulty is required")
    @Column(name = "difficulty", nullable = false)
    private String difficulty;

    @Min(value = 0, message = "Score cannot be negative")
    @Column(name = "score", nullable = false)
    private int score;

    @Min(value = 0, message = "Correct answers cannot be negative")
    @Column(name = "correct_answers", nullable = false)
    private int correctAnswers;

    @Min(value = 0, message = "Total questions cannot be negative")
    @Column(name = "total_questions", nullable = false)
    private int totalQuestions;

    @Min(value = 0, message = "Duration cannot be negative")
    @Column(name = "duration_seconds", nullable = false)
    private long durationSeconds;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
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
