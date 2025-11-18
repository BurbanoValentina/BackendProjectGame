package com.example.gamebackend.model;

/**
 * Representa una pregunta en el modo multijugador
 * Patrón: Value Object
 */
public class MultiplayerQuestion {
    private int id;
    private String prompt;
    private int answer;
    private long createdAt;

    public MultiplayerQuestion(int id, String prompt, int answer) {
        this.id = id;
        this.prompt = prompt;
        this.answer = answer;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isCorrect(int userAnswer) {
        return userAnswer == answer;
    }
}
