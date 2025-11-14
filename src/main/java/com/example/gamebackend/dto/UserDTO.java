package com.example.gamebackend.dto;

public class UserDTO {
    private Long id;
    private String username;
    private String nickname;
    private Integer highScore;
    
    // Constructors
    public UserDTO() {}
    
    public UserDTO(Long id, String username, String nickname, Integer highScore) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.highScore = highScore;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public Integer getHighScore() {
        return highScore;
    }
    
    public void setHighScore(Integer highScore) {
        this.highScore = highScore;
    }
}
