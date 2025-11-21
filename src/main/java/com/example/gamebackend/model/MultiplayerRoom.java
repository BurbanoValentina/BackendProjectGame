package com.example.gamebackend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MultiplayerRoom implements the Entity/Model pattern for multiplayer matches.
 */
@Document(collection = "multiplayer_rooms")
public class MultiplayerRoom {
    @Id
    private String roomCode;

    private final List<MultiplayerPlayer> players = new ArrayList<>();

    private final List<MultiplayerQuestion> questions = new ArrayList<>();

    private int currentQuestionIndex;

    private RoomStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    private String hostPlayerId;

    public enum RoomStatus {
        WAITING,    // Waiting for players
        PLAYING,    // Match running
        FINISHED    // Match finished
    }

    public MultiplayerRoom(String roomCode, String hostPlayerId) {
        this.roomCode = roomCode;
        this.hostPlayerId = hostPlayerId;
        this.currentQuestionIndex = 0;
        this.status = RoomStatus.WAITING;
        this.createdAt = LocalDateTime.now();
    }

    public MultiplayerRoom() {
        this.status = RoomStatus.WAITING;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode == null ? null : roomCode.toUpperCase();
    }

    public List<MultiplayerPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<MultiplayerPlayer> players) {
        this.players.clear();
        if (players == null) {
            return;
        }
        for (MultiplayerPlayer player : players) {
            if (player != null) {
                addPlayer(player);
            }
        }
    }

    public void addPlayer(MultiplayerPlayer player) {
        if (player == null) {
            return;
        }
        player.setRoom(this);
        this.players.add(player);
    }

    public void removePlayer(String playerId) {
        if (playerId == null) {
            return;
        }
        this.players.removeIf(p -> {
            if (p == null) {
                return false;
            }
            boolean matches = p.getId().equals(playerId);
            if (matches) {
                p.setRoom(null);
            }
            return matches;
        });
    }

    public List<MultiplayerQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<MultiplayerQuestion> questions) {
        this.questions.clear();
        if (questions != null) {
            for (MultiplayerQuestion question : questions) {
                if (question != null) {
                    this.questions.add(question);
                }
            }
        }
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public void setCurrentQuestionIndex(int currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
    }

    public void nextQuestion() {
        this.currentQuestionIndex++;
    }

    public MultiplayerQuestion getCurrentQuestion() {
        if (currentQuestionIndex < questions.size()) {
            return questions.get(currentQuestionIndex);
        }
        return null;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public String getHostPlayerId() {
        return hostPlayerId;
    }

    public void setHostPlayerId(String hostPlayerId) {
        this.hostPlayerId = hostPlayerId;
    }

    public boolean isHost(String playerId) {
        return hostPlayerId.equals(playerId);
    }

    public boolean canStart() {
        return players.size() >= 2 && status == RoomStatus.WAITING;
    }

    public boolean isFinished() {
        return currentQuestionIndex >= questions.size() || status == RoomStatus.FINISHED;
    }
}
