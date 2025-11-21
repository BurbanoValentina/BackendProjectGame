package com.example.gamebackend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * MultiplayerRoom implements the Entity/Model pattern for multiplayer matches.
 */
public class MultiplayerRoom {
    private String roomCode;
    private List<MultiplayerPlayer> players;
    private List<MultiplayerQuestion> questions;
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
        this.players = new ArrayList<>();
        this.questions = new ArrayList<>();
        this.currentQuestionIndex = 0;
        this.status = RoomStatus.WAITING;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public List<MultiplayerPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<MultiplayerPlayer> players) {
        this.players = players;
    }

    public void addPlayer(MultiplayerPlayer player) {
        this.players.add(player);
    }

    public void removePlayer(String playerId) {
        this.players.removeIf(p -> p.getId().equals(playerId));
    }

    public List<MultiplayerQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<MultiplayerQuestion> questions) {
        this.questions = questions;
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
