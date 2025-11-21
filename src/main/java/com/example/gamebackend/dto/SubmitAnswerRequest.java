package com.example.gamebackend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO used to send an answer in multiplayer mode.
 */
public class SubmitAnswerRequest {
    
    @NotBlank(message = "Room code is required")
    private String roomCode;
    
    @NotBlank(message = "Player id is required")
    private String playerId;
    
    @Min(value = 0, message = "Answer must be zero or positive")
    @Max(value = 999, message = "Answer must contain at most three digits")
    private int answer;
    
    @Min(value = 0, message = "Response time must be positive")
    private long responseTime;

    public SubmitAnswerRequest() {
    }

    public SubmitAnswerRequest(String roomCode, String playerId, int answer, long responseTime) {
        this.roomCode = roomCode;
        this.playerId = playerId;
        this.answer = answer;
        this.responseTime = responseTime;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }
}
