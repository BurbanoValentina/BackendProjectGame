package com.example.gamebackend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para enviar una respuesta en el modo multijugador
 */
public class SubmitAnswerRequest {
    
    @NotBlank(message = "El código de sala es requerido")
    private String roomCode;
    
    @NotBlank(message = "El ID del jugador es requerido")
    private String playerId;
    
    private int answer;
    
    @Min(value = 0, message = "El tiempo de respuesta debe ser positivo")
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
