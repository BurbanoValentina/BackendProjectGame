package com.example.gamebackend.model;

/**
 * Representa un jugador en una sala multijugador
 * Patrón: Entity/Model con Builder Pattern
 */
public class MultiplayerPlayer {
    private String id;
    private String username;
    private int score;
    private int answeredCount;
    private long totalResponseTime; // en milisegundos
    private boolean isBot;
    private boolean isReady;

    public MultiplayerPlayer(String id, String username) {
        this.id = id;
        this.username = username;
        this.score = 0;
        this.answeredCount = 0;
        this.totalResponseTime = 0;
        this.isBot = false;
        this.isReady = false;
    }

    public MultiplayerPlayer(String id, String username, boolean isBot) {
        this(id, username);
        this.isBot = isBot;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int points) {
        this.score += points;
    }

    public int getAnsweredCount() {
        return answeredCount;
    }

    public void setAnsweredCount(int answeredCount) {
        this.answeredCount = answeredCount;
    }

    public void incrementAnsweredCount() {
        this.answeredCount++;
    }

    public long getTotalResponseTime() {
        return totalResponseTime;
    }

    public void setTotalResponseTime(long totalResponseTime) {
        this.totalResponseTime = totalResponseTime;
    }

    public void addResponseTime(long responseTime) {
        this.totalResponseTime += responseTime;
    }

    public double getAverageResponseTime() {
        if (answeredCount == 0) return 0;
        return (double) totalResponseTime / answeredCount;
    }

    public boolean isBot() {
        return isBot;
    }

    public void setBot(boolean bot) {
        isBot = bot;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    /**
     * Builder Pattern para crear jugadores
     */
    public static class Builder {
        private String id;
        private String username;
        private boolean isBot = false;

        public Builder(String id, String username) {
            this.id = id;
            this.username = username;
        }

        public Builder bot(boolean isBot) {
            this.isBot = isBot;
            return this;
        }

        public MultiplayerPlayer build() {
            return new MultiplayerPlayer(id, username, isBot);
        }
    }
}
