package com.example.gamebackend.model;

/**
 * MultiplayerQuestion models the Value Object pattern inside multiplayer rooms.
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

    public MultiplayerQuestion() {
        // JPA constructor
    }

    // Getters and setters
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
