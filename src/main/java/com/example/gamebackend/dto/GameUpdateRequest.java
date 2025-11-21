package com.example.gamebackend.dto;

import jakarta.validation.constraints.Min;

public class GameUpdateRequest {

    @Min(value = 0, message = "Score cannot be negative")
    private int score;

    @Min(value = 0, message = "Correct answers cannot be negative")
    private int correctAnswers;

    @Min(value = 0, message = "Total questions cannot be negative")
    private int totalQuestions;

    @Min(value = 0, message = "Duration cannot be negative")
    private long durationSeconds;

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
}
