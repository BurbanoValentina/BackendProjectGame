package com.example.gamebackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * MultiplayerQuestion models the Value Object pattern inside multiplayer rooms.
 */
@Embeddable
public class MultiplayerQuestion {
    @Column(name = "question_number")
    private int id;

    @Column(name = "question_prompt", nullable = false)
    private String prompt;

    @Column(name = "question_answer", nullable = false)
    private int answer;

    @Column(name = "question_created_at")
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
