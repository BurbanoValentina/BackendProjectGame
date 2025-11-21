package com.example.gamebackend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * MultiplayerRoom implements the Entity/Model pattern for multiplayer matches.
 */
@Entity
@Table(name = "multiplayer_rooms")
public class MultiplayerRoom {
    @Id
    @Column(name = "room_code", length = 16)
    private String roomCode;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<MultiplayerPlayer> players = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "multiplayer_room_questions", joinColumns = @JoinColumn(name = "room_code"))
    @OrderColumn(name = "question_order")
    private final List<MultiplayerQuestion> questions = new ArrayList<>();

    @Column(name = "current_question_index", nullable = false)
    private int currentQuestionIndex;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RoomStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "host_player_id", nullable = false)
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
        // JPA constructor
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = RoomStatus.WAITING;
        }
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
