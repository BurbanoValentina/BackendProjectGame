package com.example.gamebackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gamebackend.model.MultiplayerPlayer;

public interface MultiplayerPlayerRepository extends JpaRepository<MultiplayerPlayer, String> {
    List<MultiplayerPlayer> findByRoomRoomCode(String roomCode);
}
