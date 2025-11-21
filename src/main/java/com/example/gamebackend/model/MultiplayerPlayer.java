package com.example.gamebackend.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * MultiplayerPlayer represents the Entity/Model and exposes a Builder Pattern for bots.
 */
@Entity
@Table(name = "multiplayer_players")
public class MultiplayerPlayer {
    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "score", nullable = false)
    private int score;

    @Column(name = "answered_count", nullable = false)
    private int answeredCount;

    @Column(name = "total_response_time", nullable = false)
    private long totalResponseTime; // stored in milliseconds

    @Column(name = "is_bot", nullable = false)
    private boolean isBot;

    @Column(name = "is_ready", nullable = false)
    private boolean isReady;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_code")
    @JsonIgnore
    private MultiplayerRoom room;

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

    public MultiplayerPlayer() {
        // JPA constructor
    }

    @PrePersist
    protected void ensureId() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
    }

    // Getters and setters
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

    public MultiplayerRoom getRoom() {
        return room;
    }

    public void setRoom(MultiplayerRoom room) {
        this.room = room;
    }

    /**
     * Builder Pattern to simplify bot creation.
     */
    public static class Builder {
        private final String id;
        private final String username;
        private boolean isBot = false;
        private MultiplayerRoom room;

        public Builder(String id, String username) {
            this.id = id;
            this.username = username;
        }

        public Builder bot(boolean isBot) {
            this.isBot = isBot;
            return this;
        }

        public Builder setRoom(MultiplayerRoom room) {
            this.room = room;
            return this;
        }

        public MultiplayerPlayer build() {
            MultiplayerPlayer player = new MultiplayerPlayer(id, username, isBot);
            player.setRoom(this.room);
            return player;
        }
    }
}
